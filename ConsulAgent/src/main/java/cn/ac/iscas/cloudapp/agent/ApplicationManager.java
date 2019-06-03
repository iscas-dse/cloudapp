package cn.ac.iscas.cloudapp.agent;

import java.io.IOException;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import cn.ac.iscas.cloudapp.agent.entity.manager.AppSpecifiEntityManager;
import cn.ac.iscas.cloudapp.agent.entity.manager.AppTemplateFileManager;
import cn.ac.iscas.cloudapp.agent.entity.manager.AppVolumeManager;
import cn.ac.iscas.cloudapp.agent.entity.manager.AttributeManager;
import cn.ac.iscas.cloudapp.agent.entity.manager.ContainerManager;
import cn.ac.iscas.cloudapp.agent.entity.manager.ServiceManager;
import cn.ac.iscas.cloudapp.agent.packet.PacketManager;
import cn.ac.iscas.cloudeploy.v2.packet.Packet;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity;
import net.lingala.zip4j.exception.ZipException;
import cn.ac.iscas.cloudapp.agent.entity.manager.ConsulTemplateManager;

/**
 * manager app's information.
 * @author RichardLcc
 */
@Service
public class ApplicationManager {
	private static Logger logger = LoggerFactory.getLogger(ApplicationManager.class);
	@Autowired
	private AppTemplateFileManager templateManager;
	@Autowired
	private AppSpecifiEntityManager specifiEntityManager;
	@Autowired
	private ServiceManager serviceManager;
	@Autowired
	private AttributeManager attributeManager;
	@Autowired
	private AppVolumeManager volumeManager;
	@Autowired
	private ContainerManager containerManager;
	@Autowired
	private ConsulTemplateManager temManager;
	@Autowired
	private AppCreater executor;
	@Autowired
	private PacketManager packetManager;
	
	public void createApp(String urlString) throws MalformedURLException, IOException, ZipException{
		Preconditions.checkArgument(!Strings.isNullOrEmpty(urlString), "url can't be null or empty");
		
		Packet packet = packetManager.getPacketFromURL(urlString);
		Optional<SpecificEntity> specifiEntity = specifiEntityManager.store(packet.getSpecificEntityFile());
		if(!specifiEntity.isPresent()){
			logger.error("create app failed based on url: " + urlString);
			return;
		}else{
			executor.register(specifiEntity.get());
			logger.info("create app success:" + specifiEntity.get().getName());
		}
	}
	
	/**
	 * delete app
	 * @param appName
	 */
	public void deleteApp(String appName){
		Optional<SpecificEntity> application = specifiEntityManager.get(appName);
		if(application.isPresent()){
			serviceManager.unregisterServices(application.get().getServices());
			attributeManager.deleteAttributes(application.get(), application.get().getAttributes());
			specifiEntityManager.deleteSpecificEntity(appName);
		}
		logger.info("delete app's template file");
		templateManager.delete(appName);
		temManager.removeTemplates(application.get().getContainer().getContainerName());
		containerManager.deleteContainer(application.get().getContainer().getContainerName());
		volumeManager.deleteAllVolumeFile(appName);
	}
	
	public void startApp(String appName){
		containerManager.startContainer(appName);
	}
	
	public void stopApp(String appName){
		containerManager.stopContainer(appName);
	}
}

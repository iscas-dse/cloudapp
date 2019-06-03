package cn.ac.iscas.cloudapp.agent;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import cn.ac.iscas.cloudapp.agent.entity.manager.AppTemplateFileManager;
import cn.ac.iscas.cloudapp.agent.entity.manager.AppVolumeManager;
import cn.ac.iscas.cloudapp.agent.entity.manager.AttributeManager;
import cn.ac.iscas.cloudapp.agent.entity.manager.ContainerManager;
import cn.ac.iscas.cloudapp.agent.entity.manager.ServiceManager;
import cn.ac.iscas.cloudapp.agent.util.ForEachHelper;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Check;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Service;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Template;
import cn.ac.iscas.cloudapp.agent.entity.manager.ConsulTemplateManager;

import com.google.common.base.Optional;
import com.spotify.docker.client.messages.ContainerInfo;

/**
 * @author RichardLcc register service based on service specific file
 */
@org.springframework.stereotype.Service
public class AppCreater {
	private static Logger logger = LoggerFactory.getLogger(AppCreater.class);

	@Value("#{configs['host_ip']}")
	private String ip;

	@Autowired
	private ContainerManager containerManager;
	@Autowired
	private ConsulTemplateManager consulTemplateManager;
	@Autowired
	private ServiceManager serviceManager;
	@Autowired
	private AppTemplateFileManager appTemplateManager;
	@Autowired
	private AttributeManager attributeManager;
	@Autowired
	private AppVolumeManager volumeManager;

	public void register(SpecificEntity application) {
		for (Template template : ForEachHelper.of(application.getTemplates())) {
			String fileUrl = template.getSource();
			String targetTemplate = appTemplateManager.store(fileUrl, application.getName()).get();
			template.setSource(targetTemplate.replace("\\", "/"));

			Optional<Path> target = volumeManager.createVolumeFile(application.getName(), template);
			if (!target.isPresent())
				continue;
			String volume = target.get() + ":" + template.getTarget() + ":" + "rw";
			List<String> volumes = application.getContainer().getVolumes();
			if (null == volumes) {
				volumes = new ArrayList<String>();
			}
			volumes.add(volume);
			template.setTarget(target.get().toString());
		}
		
		String id = createContainer(application);
		
		List<Template> templates = application.getTemplates();
		for (Template template : ForEachHelper.of(templates)) {
			if (template.getCommand() != null && !template.getCommand().equals("")) {
				String command = containerManager.getCommandBuilder().execCommand(id, template.getCommand());
				template.setCommand(command);
			} else {
				String command = containerManager.getCommandBuilder().restartComand(id);
				template.setCommand(command);
			}
		}
		try {
			consulTemplateManager.addTemplates(templates, application.getName());
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("failed to store template info for" + application.getName());
		}
	}

	private String createContainer(SpecificEntity application) {
		List<Service> services = application.getServices();
		String containerName = application.getContainer().getContainerName();
		SpecificEntity.Container specContainer = application.getContainer();
		Optional<ContainerInfo> containerInfo = containerManager.createContainer(specContainer, containerName);
		String containerId = containerInfo.get().id();
		registerServices(services, containerInfo, containerId);
		attributeManager.registerAttributes(application,application.getAttributes());
		return containerInfo.get().id();
	}

	private void registerServices(List<Service> services, Optional<ContainerInfo> containerInfo, String containerId) {
		for (Service service : services) {
			service.setAddress(ip);
			if (service.getChecks() == null || service.getChecks().size() == 0) {
				SpecificEntity.Check check = new SpecificEntity.Check();
				check.setScript("echo hello");
				check.setInterval(20L);
				List<Check> checks = new ArrayList<>();
				checks.add(check);
				service.setChecks(checks);
			}
			for (SpecificEntity.Check check : ForEachHelper.of(service.getChecks())) {
				if (check.getScript() != null) {
					String script = check.getScript();
					script = containerManager.getCommandBuilder().execCommand(containerId, script);
					check.setScript(script);
				}
			}
			serviceManager.registerService(service);
		}
	}
}

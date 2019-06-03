package cn.ac.iscas.cloudapp.agent.entity.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

import cn.ac.iscas.cloudapp.agent.util.FileDownloader;
import cn.ac.iscas.cloudapp.agent.util.YamlUtils;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity;
import jersey.repackaged.com.google.common.base.Preconditions;

/**
 * manage Application SpecifiEntity meta data.
 * it will be responsible for storing,updating and deleting of SpecifiEntity
 * @author RichardLcc
 *
 */
@Service
public class AppSpecifiEntityManager {
	private static Logger logger = LoggerFactory.getLogger(AppSpecifiEntityManager.class);
	// directory of application configuration file
	private Path dirPath;
	
	@Autowired
	private FileDownloader downloader;
	
	@Autowired
	public AppSpecifiEntityManager(@Value("#{configs['config_path']}") String path){
		dirPath = Paths.get(path);
	}
	
	@PostConstruct
	private void initPath() throws IOException{
		if(!Files.exists(dirPath)){
			Files.createDirectories(dirPath);
		}
	}
	
	public Optional<SpecificEntity> store(String path) throws IOException{
		Preconditions.checkArgument(!Strings.isNullOrEmpty(path), "path can't be null or empty");
		Path filePath = Paths.get(path);
		SpecificEntity entity = YamlUtils.loadAs(filePath, SpecificEntity.class);
		
		String appName = entity.getName();
		Path targetPath = dirPath.resolve(appName + ".yaml");
		//TODO 测试需要改成mv命令
		Files.copy(filePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
		return Optional.of(entity);
	}
	
	public Optional<SpecificEntity> get(String appName){
		Yaml yaml = new Yaml();
		Path filePath = dirPath.resolve(appName + ".yaml");
		try {
			SpecificEntity entity = yaml.loadAs(Files.newInputStream(filePath), SpecificEntity.class);
			return Optional.of(entity);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.absent();
	}
	
	public void deleteSpecificEntity(String appName){
		logger.info("delete app's specifientity file");
		Path path = dirPath.resolve(appName + ".yaml");
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

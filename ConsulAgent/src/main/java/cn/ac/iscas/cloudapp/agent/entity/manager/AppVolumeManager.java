package cn.ac.iscas.cloudapp.agent.entity.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity;
/**
 * manager all volume files of container
 * @author RichardLcc
 *
 */
@Service
public class AppVolumeManager {
	private static Logger logger = LoggerFactory.getLogger(AppVolumeManager.class);
	private Path volumePath;

	@Autowired
	public AppVolumeManager(@Value("#{configs['volume_path']}") String path) {
		logger.info("init agent volume path :" + path);
		volumePath = Paths.get(path);
	}
	
	@PostConstruct
	private void initPath() throws IOException{
		if(!Files.exists(volumePath)){
			Files.createDirectories(volumePath);
		}
	}

	private Optional<Path> createVolumeDirectory(String appName) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(appName), "appName can't be null or empty");
		
		Path dirPath = volumePath.resolve(appName);
		if (Files.exists(dirPath) && Files.isDirectory(dirPath)) {
			return Optional.of(dirPath);
		} else {
			try {
				Files.createDirectories(dirPath);
				logger.info("create volume directory for app: " + appName + " directory: " + dirPath);
				return Optional.of(dirPath);
			} catch (IOException e) {
				logger.info("create volume directory for app: " + appName + " directory: " + dirPath + "failed");
				e.printStackTrace();
				return Optional.absent();
			}
		}
	}

	public Optional<Path> createVolumeFile(String appName, SpecificEntity.Template template){
		Preconditions.checkArgument(!Strings.isNullOrEmpty(appName), "appName can't be null or empty");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(template.getTarget()), "template's target attribute can't be null or empty");
		
		Optional<Path> dirPath = createVolumeDirectory(appName);
		if (!dirPath.isPresent()) return Optional.absent();
		Path containerTarget = Paths.get(template.getTarget());
		Path target = dirPath.get().resolve(containerTarget.getFileName());
		if (!Files.exists(target)) {
			try {
				Files.createFile(target);
			} catch (IOException e) {
				logger.error("create volume file" + target + " for " + appName + "failed");
				e.printStackTrace();
				return Optional.absent();
			}
		}
		return Optional.of(target);
	}
	
	public void deleteAllVolumeFile(String appName){
		Preconditions.checkArgument(!Strings.isNullOrEmpty(appName), "appName can't be null or empty");
		
		Path dirPath = volumePath.resolve(appName);
		if(Files.exists(dirPath)){
			try {
				Files.delete(dirPath);
				logger.info("delete volumes file for " + appName);
			} catch (IOException e) {
				logger.error("delete volumes file for " + appName + "failed");
				e.printStackTrace();
			}
		}
	}
}

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

import cn.ac.iscas.cloudapp.agent.util.FileDownloader;

/**
 * manage templates of application.
 * make a directory under agent's template path for application, manager will download all
 * templates to this directory.
 * @author RichardLcc
 *
 */
@Service
public class AppTemplateFileManager {
	private static Logger logger = LoggerFactory.getLogger(AppTemplateFileManager.class);
	private Path templatePath;
	
	@Autowired
	private FileDownloader downloader;
	
	@Autowired
	public AppTemplateFileManager(@Value("#{configs['template_path']}") String path){
		logger.info("init agent template path:" + path);
		templatePath = Paths.get(path);
	}
	
	@PostConstruct
	private void initPath() throws IOException{
		if(!Files.exists(templatePath)){
			Files.createDirectories(templatePath);
		}
	}
	
	/**
	 * store application's template, if directory(templatePath/appName) doesn't exist
	 * it will create a directory under templatePath
	 * @param url url of template file
	 * @param appName application's name
	 * @return the path of template file on file system
	 */
	public Optional<String> store(String url, String appName){
		logger.info("storing template for " + appName + "with url: " + url);
		Path dirPath = templatePath.resolve(appName);
		try{
			if(!Files.exists(dirPath) || !Files.isDirectory(dirPath)){
				if(Files.exists(dirPath)){//如果不是文件夹删除
					Files.delete(dirPath);
				}
				Files.createDirectories(dirPath);
			}
			String filepath = downloader.downloadFile(url, dirPath, ".ctml");
			return Optional.fromNullable(filepath);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("store template failed: " + url + "appName: " + appName);
		}
		return Optional.absent();
	}
	
	/**
	 * delete all template of application
	 * @param appName
	 */
	public void delete(String appName){
		logger.info("delete templates of %s", appName);
		Path dirPath = templatePath.resolve(appName);
		try {
			Files.deleteIfExists(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("delete templates failed");
		}
	}
}

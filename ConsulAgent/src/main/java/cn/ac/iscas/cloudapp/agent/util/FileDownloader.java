package cn.ac.iscas.cloudapp.agent.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.ac.iscas.cloudapp.agent.command.CommandRunner;

@Component
public class FileDownloader {
	private static Logger logger = LoggerFactory.getLogger(FileDownloader.class);
	private Path dirPath;
	private Path templatePath;
	
	@Autowired
	public FileDownloader(@Value("#{configs['config_path']}") String path, @Value("#{configs['template_path']}") String templatePath){
		logger.info("init filedownloader with config_path :" + path);
		dirPath = Paths.get(path);
		this.templatePath = Paths.get(templatePath);
	}
	
	public String downloadFile(String url) throws IOException{
		return downloadFile(url, dirPath, ".yaml");
	}
	
	public String downloadFile(String url, Path path, String extension) throws IOException{
		URL source = new URL(url);
		String fileName = RandomUtils.randomString() + extension;
		Path target = path.resolve(fileName);
		logger.info("download file " + source + " as " + fileName);
		FileUtils.copyURLToFile(source, target.toFile());
		return target.toFile().getAbsolutePath();
	}
	
//	//just for test
//	public String downloadFileForTemplate(String url) throws IOException{
////		Path path = Paths.get("src/test/resources/templates");
////		String file = downloadFile(url, path, ".ctml");
////		String content = Files.toString(new File(file), Charsets.UTF_8);
//		String fileName = RandomUtils.randomString() + ".ctml";
//		Path target = templatePath.resolve(fileName);
//		runner.run("curl -o "  + target.toString().replace("\\", "/") + " " + url);
//		return target.toString();
//	}
	
	public String downloadFileForTemplate(String url) throws IOException{
//		String fileName = RandomUtils.randomString() + ".ctml";
//		Path target = templatePath.resolve(fileName);
		String file = downloadFile(url, templatePath, ".ctml");
		return file;
	}
}

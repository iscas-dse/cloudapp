package cn.ac.iscas.cloudapp.agent.entity.manager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudapp.agent.command.CommandRunner;
import cn.ac.iscas.cloudapp.agent.util.ForEachHelper;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.io.Files;
/**
 * 
 * @author RichardLcc
 *
 */
@Service
public class ConsulTemplateManager {
	private static Logger logger = LoggerFactory.getLogger(ConsulTemplateManager.class);
	private List<Integer> pids;
	
	@Value("#{configs['pid_consul_template']}")
	private String pidCommand;
	@Value("#{configs['reload_consul_template']}")
	private String reloadCommandPrefix;
	
	private Path templateConfigPath;
	
	@Autowired
	private CommandRunner commandRunner;
	
	@Autowired
	public ConsulTemplateManager(@Value("#{configs['consul_template_configpath']}") String consul_template_configpath) {
		templateConfigPath = Paths.get(consul_template_configpath);
	}
	
	@PostConstruct
	private void initPath() throws IOException{
		if(!java.nio.file.Files.exists(templateConfigPath)){
			java.nio.file.Files.createDirectories(templateConfigPath);
		}
	}
	
	public void init(){
		pids = findPidOfConsulTemplate();
	}
	
	private List<Integer> findPidOfConsulTemplate() {
		List<Integer> res = new ArrayList<Integer>();
		Optional<InputStream> in = commandRunner.run(pidCommand);
		if(in.isPresent()){
			Scanner scanner = new Scanner(in.get());
			while(scanner.hasNext()){
				res.add(scanner.nextInt());
			}
			scanner.close();
		}
		return res;
	}
	
	public void removeTemplates(String containerName){
		String cfgFileName = containerName + ".cfg";
		Path target = templateConfigPath.resolve(cfgFileName);
		target.toFile().delete();
	}
	
	public void addTemplates(List<SpecificEntity.Template> templates, String fileName) throws IOException{
		StringBuilder builder = new StringBuilder();
		String lineSep=System.getProperty("line.separator");
		for(SpecificEntity.Template template : ForEachHelper.of(templates)){
			builder.append(templateFile(template.getSource(), template.getTarget(), template.getCommand()));
			builder.append(lineSep);
		}
		storeContent(builder.toString(), fileName);
		reloadConfiguration();
	}
	
	public void addTemplate(String templatePath, String outputPath, String command, String fileName) throws IOException{
		String content = templateFile(templatePath, outputPath, command);
		storeContent(content, fileName);
		reloadConfiguration();
	}
	
	private void storeContent(String content, String fileName) throws IOException{
		logger.info("store content as file: " + fileName + "\ncontent: " + content);
		String cfgFileName = fileName + ".cfg";
		Path target = templateConfigPath.resolve(cfgFileName);
		Files.write(content, target.toFile(), Charsets.UTF_8);
	}
	
	private String templateFile(String templatePath, String outputPath, String command){
		StringBuilder templateBuilder = new StringBuilder();
		String lineSep=System.getProperty("line.separator");
		templateBuilder.append("template {").append(lineSep)
			.append("source = \"").append(templatePath).append("\"").append(lineSep)
			.append("destination = \"").append(outputPath).append("\"").append(lineSep);
		if(command != null && !command.equals("")){
			templateBuilder.append("command = \"").append(command).append("\"").append(lineSep);
		}
		templateBuilder.append("}").append(lineSep);
		return templateBuilder.toString();
	}
	
	public void reloadConfiguration(){
		StringBuilder command = new StringBuilder();
		command.append(reloadCommandPrefix).append(" ");
		init();
		for(Integer pid : pids){
			command.append(pid).append(" ");
		}
		commandRunner.run(command.toString());
	}
}

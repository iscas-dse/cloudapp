package cn.ac.iscas.cloudapp.agent.consul;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class ConsulTemplateServer {
	private ProcessBuilder builder;
	private Process process;
	private String workdir;
	
	public ConsulTemplateServer(){
		workdir = "src/test/resources/consulTemplate/";
		List<String> commandConfig = new ArrayList<>();
		commandConfig.add(workdir + "consul-template.exe");
		commandConfig.add("-consul=127.0.0.1:8500");
		commandConfig.add("-retry=1s");
		commandConfig.add("-config="+ workdir + "config/");
		commandConfig.add("-log-level=info");
		builder = new ProcessBuilder(commandConfig);
		builder.inheritIO();
	}
	
	public void start() throws IOException {
		if(process == null){
			process = builder.start();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void stop(){
		if(process != null){
			process.destroy();
			process = null;
		}
	}
	
	public void close() throws IOException  {
		stop();
		cleanData();
	}
	
	public void reload() throws IOException{
		stop();
		start();
	}
	
	private void cleanData() throws IOException{
		Path path = Paths.get(workdir).resolve("config");
		FileUtils.cleanDirectory(path.toFile());
	}
}

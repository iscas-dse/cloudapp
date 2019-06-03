package cn.ac.iscas.cloudapp.agent.consul;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class ConsulServer implements Closeable{
	private ProcessBuilder builder;
	private Process process;
	private String workdir;
	
	public ConsulServer(){
		workdir = "src/test/resources/consul/";
		List<String> commandConfig = new ArrayList<>();
		commandConfig.add(workdir + "bin/consul.exe");
		commandConfig.add("agent");
		commandConfig.add("-bind=127.0.0.1");
		commandConfig.add("-bootstrap-expect=1");
		commandConfig.add("-client=127.0.0.1");
		commandConfig.add("-data-dir="+ workdir + "data/");
		commandConfig.add("-dc=consultestServer");
		commandConfig.add("-log-level=INFO");
		commandConfig.add("-node=consulTest");
		commandConfig.add("-server=true");
		commandConfig.add("-ui-dir="+ workdir + "ui/");
		builder = new ProcessBuilder(commandConfig);
		builder.inheritIO();
	}
	
	private void cleanData() throws IOException{
		Path path = Paths.get(workdir).resolve("data");
		FileUtils.cleanDirectory(path.toFile());
	}
	
	public void start() throws IOException{
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
	
	@Override
	public void close() throws IOException {
		stop();
		cleanData();
	}
}

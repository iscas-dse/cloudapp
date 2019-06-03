package cn.ac.iscas.cloudapp.agent.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;

@Service
class LocalCommandRunner implements CommandRunner{
	private static Logger logger = LoggerFactory.getLogger(LocalCommandRunner.class);
	public Optional<InputStream> run(String command){
		logger.info("run command" + command);
		InputStream err = null;
		try {
			Process process = Runtime.getRuntime().exec(command);
			InputStream in = process.getInputStream();
			err = process.getErrorStream();
			int exitValue = process.waitFor();
			if(exitValue == 0)
				return Optional.of(in);
			else{
				try(BufferedReader reader = new BufferedReader(new InputStreamReader(err))){
					String line = null;
					while((line = reader.readLine()) != null){
						logger.error(line);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return Optional.absent();
	}
}
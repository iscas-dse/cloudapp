package cn.ac.iscas.cloudapp.agent.command;

import java.io.InputStream;

import com.google.common.base.Optional;

public interface CommandRunner {
	public Optional<InputStream> run(String command);
}

package cn.ac.iscas.cloudapp.agent.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;

import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.jcabi.ssh.SSHByPassword;
import com.jcabi.ssh.Shell;
//@Service
public class ShellCommandRunner implements CommandRunner{
	private Shell shell;
	public ShellCommandRunner() throws UnknownHostException {
		shell = new SSHByPassword("133.133.135.222", 22, "root", "richard");
	}
	@Override
	public Optional<InputStream> run(String command) {
		try {
			Pipe pipe = Pipe.open();
			OutputStream stdout = Channels.newOutputStream(pipe.sink());
			new Shell.Safe(shell).exec(command, null, stdout, null);
			return Optional.of(Channels.newInputStream(pipe.source()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.absent();
	}
}

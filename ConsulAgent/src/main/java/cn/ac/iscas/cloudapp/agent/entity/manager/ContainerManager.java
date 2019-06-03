package cn.ac.iscas.cloudapp.agent.entity.manager;

import com.google.common.base.Optional;
import com.spotify.docker.client.messages.ContainerInfo;

import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Container;

public interface ContainerManager {
	public static interface DockerCommandBuilder {
		public String restartComand(String containerId);
		public String execCommand(String id, String command);
	}
	public String findContainer(String name);
	public boolean startContainer(String name);
	public boolean stopContainer(String name);
	public Optional<ContainerInfo> createContainer(Container app, String containerName);
	public DockerCommandBuilder getCommandBuilder();
	public boolean deleteContainer(String containerName);
}

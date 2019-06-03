package cn.ac.iscas.cloudapp.agent.docker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudapp.agent.entity.manager.ContainerManager;
import cn.ac.iscas.cloudapp.agent.util.ForEachHelper;
import cn.ac.iscas.cloudapp.agent.util.JsonUtils;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Optional;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerConfig.Builder;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;

@Service
public class DockerServiceImpl implements ContainerManager {
	private static Logger logger = LoggerFactory
			.getLogger(DockerServiceImpl.class);
	private String dockerUri;
	private DockerClient docker;
	private WeakHashMap<String, String> containerMes;
	@Value("#{configs['host_ip']}")
	private String ip;

	@Autowired
	public DockerServiceImpl(@Value("#{configs['docker.uri']}") String dockerUri) {
		this.dockerUri = dockerUri;
		docker = DefaultDockerClient.builder().uri("http://" + dockerUri)
				.build();
		containerMes = new WeakHashMap<String, String>();
	}

	@Override
	public Optional<ContainerInfo> createContainer(
			SpecificEntity.Container app, String containerName) {
		String containerId = findContainer(app.getContainerName());
		if (containerId == null) {
			try {
				Builder configBuilder = createContainerConfig(app);
				ContainerCreation container = docker.createContainer(
						configBuilder.build(), containerName);
				containerMes.put(containerName, container.id());
				containerId = container.id();
			} catch (DockerException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.info("create container :" + containerId +" " + containerName);
		try {
			startContainer(app, containerId);
			return Optional.of(docker.inspectContainer(containerId));
		} catch (DockerException | InterruptedException e) {
			e.printStackTrace();
			return Optional.absent();
		}
	}

	private void startContainer(SpecificEntity.Container app, String containerId){
		try {
			ContainerInfo containerInfo = docker.inspectContainer(containerId);
			HostConfig hostconfig = createHostConfig(app, containerInfo);
			logger.info(JsonUtils.convertToJson(hostconfig));
			docker.startContainer(containerId, hostconfig);
		} catch (DockerException | InterruptedException e) {
			logger.info(e.getMessage());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Builder createContainerConfig(SpecificEntity.Container app) {
		Builder configBuilder = ContainerConfig.builder()
				.hostname(app.getHostname()).domainname(app.getDomainname())
				.user(app.getUser()).memory(app.getMemory())
				.cpuShares(app.getCpuShares()).cpuset(app.getCpuset())
				.attachStdin(app.getAttachStdin())
				.attachStderr(app.getAttachStderr())
				.attachStdout(app.getAttachStdout()).tty(app.getTty())
				.openStdin(app.getOpenStdin()).stdinOnce(app.getStdinOnce())
				.image(app.getImage()).workingDir(app.getWorkingDir())
				.networkDisabled(app.getNetworkDisabled());
		if (app.getPortSpecs() != null)
			configBuilder.portSpecs(app.getPortSpecs());
		if (app.getEnv() != null)
			configBuilder.env(app.getEnv());
		if (app.getCmd() != null)
			configBuilder.cmd(app.getCmd());
		if (app.getEntrypoint() != null)
			configBuilder.entrypoint(app.getEntrypoint());
		if (app.getExposedPorts() != null)
			configBuilder.exposedPorts(app.getExposedPorts());
		if (app.getOnBuild() != null)
			configBuilder.onBuild(app.getOnBuild());
		return configBuilder;
	}

	private HostConfig createHostConfig(SpecificEntity.Container app, ContainerInfo containerInfo) {
		Set<String> exposedPorts = containerInfo.config().exposedPorts();
		List<SpecificEntity.Container.PortBinding> portbinds = app
				.getHostConfig().getPortBindings();
		Map<String, List<PortBinding>> configs = new HashMap<>();
		HostConfig.Builder builder = HostConfig.builder();
		int publishCount = exposedPorts.size() - portbinds.size();
		//TODO 前端需要动态获取镜像的Ports参数，一一绑定而非如此匹配。
		for(String exposedPort : exposedPorts){
			if(publishCount-- > 0){
				builder.publishAllPorts(true);
			}else{
				for (SpecificEntity.Container.PortBinding portBinding : portbinds) {
					List<PortBinding> config = new ArrayList<PortBinding>();
					PortBinding port = PortBinding.of(ip, portBinding.getTargetPort());
					config.add(port);
					configs.put(exposedPort, config);
					portbinds.remove(portBinding);
					break;
				}
			}
		}
		builder.portBindings(configs);
		if(app.getVolumes() != null) builder.binds(app.getVolumes());
		return builder.build();
	}

	public static Builder getConfigBuilder() {
		return ContainerConfig.builder();
	}

	@Override
	public String findContainer(String name) {
		ListContainersParam params = ListContainersParam.allContainers(true);
		if (!containerMes.containsKey(name)) {
			try {
				List<Container> containers = docker.listContainers(params);
				for (Container container : ForEachHelper.of(containers)) {
					for (String cname : ForEachHelper.of(container.names())) {
						containerMes.put(cname.substring(1), container.id());
					}
				}
			} catch (DockerException | InterruptedException e) {
				logger.error("list container message failed");
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return containerMes.get(name);
	}

	@Override
	public boolean startContainer(String name) {
		String id = findContainer(name);
		try {
			docker.startContainer(id);
			return true;
		} catch (DockerException | InterruptedException e) {
			e.printStackTrace();
			logger.error("start container" + name + "failed");
			logger.error(e.getMessage());
			return false;
		}
	}

	@Override
	public boolean stopContainer(String name) {
		String id = findContainer(name);
		try {
			logger.info("going to stop container : " + id);
			if(id != null ){
				docker.stopContainer(id, 3);
				logger.info("stop container : success");
				return true;
			}
		} catch (DockerException | InterruptedException e) {
			e.printStackTrace();
			logger.error("stop container" + name + "failed");
			logger.error(e.getMessage());
		}
		logger.info("stop container : false");
		return false;
	}

	@Override
	public DockerCommandBuilder getCommandBuilder() {
		return new DockerCommandBuilder() {
			private String dockerPre = "docker -H " + dockerUri;

			@Override
			public String restartComand(String containerId) {
				return dockerPre + " restart " + containerId;
			}

			@Override
			public String execCommand(String id, String command) {
				return dockerPre + " exec -d " + id + " " + command;
			}
		};
	}

	@Override
	public boolean deleteContainer(String containerName) {
		String id = findContainer(containerName);
		try {
			logger.info("going to delete container : " + id);
			if(id != null ){
				stopContainer(containerName);
				docker.removeContainer(id, true);
				containerMes.remove(containerName);
				logger.info("delete container : success");
				return true;
			}
		} catch (DockerException | InterruptedException e) {
			e.printStackTrace();
			logger.error("delete container" + containerName + "failed");
			logger.error(e.getMessage());
		}
		logger.info("delete container : false");
		return false;
	}

}

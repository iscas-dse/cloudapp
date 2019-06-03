package cn.ac.iscas.cloudeploy.v2.util;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.model.agent.ImmutableRegCheck;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.health.ServiceHealth;

import lombok.extern.log4j.Log4j;

/**
 * @author Simon Lee
 * @description
 * @date 2017-12-22
 */
@Log4j
public class ConsulUtil {

	static Consul consul = Consul.builder().withHostAndPort(HostAndPort.fromString("133.133.134.96:8500")).build();

	/**
	 * 服务注册
	 */
	public static void serviceRegister() {
		AgentClient agent = consul.agentClient();

		// 健康检测
		ImmutableRegCheck check = ImmutableRegCheck.builder().http("http://133.133.134.96:9020/health").interval("5s")
				.build();

		ImmutableRegistration.Builder builder = ImmutableRegistration.builder();
		builder.id("t-180109-161826").name("mysql").addTags("v1").address("133.133.134.96").port(31113)
				.addChecks(check);

		agent.register(builder.build());
	}

	/**
	 * 服务获取
	 */
	public static void serviceGet() {
		HealthClient client = consul.healthClient();
		String name = "mysql";
		// 获取所有服务
		log.info(client.getAllServiceInstances(name).getResponse().size());
		// 获取所有正常的服务（健康检测通过的）
		for (ServiceHealth resp : client.getHealthyServiceInstances(name).getResponse()) {
			log.info(resp);
		}
	}

	public static void main(String[] args) {
		serviceRegister();
		serviceGet();
	}
}

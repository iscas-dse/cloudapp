package cn.ac.iscas.cloudapp.agent.config;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.orbitz.consul.Consul;

@Configuration
@Profile("prod")
public class AgentConfiguration {
	@Bean
	public Consul consul(@Value("#{configs['consul_host']}") String host, @Value("#{configs['consul_port']}") int port) throws IOException{
		return Consul.newClient(host, port);
	}
}

package cn.ac.iscas.cloudapp.agent.entity.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.ImmutableRegistration.Builder;
import com.orbitz.consul.model.agent.Registration;
import com.orbitz.consul.model.agent.Registration.RegCheck;

import cn.ac.iscas.cloudapp.agent.util.ForEachHelper;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Check;

/**
 * register service and unregister service
 * @author RichardLcc
 *
 */
@Service
public class ServiceManager {
	private static Logger logger = LoggerFactory.getLogger(ServiceManager.class);
	@Autowired
	private Consul consul;
	
	private static Function<SpecificEntity.Check, Registration.RegCheck> trans =
			new Function<SpecificEntity.Check, Registration.RegCheck>() {
				@Override
				public RegCheck apply(Check input) {
					Preconditions.checkNotNull(input.getInterval(), "check interval can't be null");
					Preconditions.checkArgument(input.getInterval() >= 0, "check interval must be greater than 0");
				
					Registration.RegCheck res;
					if(input.getScript() != null){
						res = Registration.RegCheck.script(input.getScript(), input.getInterval());
					}
					else if(input.getHttp() != null){
						res = Registration.RegCheck.http(input.getHttp(), input.getInterval());
					}
					else if(input.getTtl() != null){
						res = Registration.RegCheck.ttl(input.getInterval());
					}else{
						throw new IllegalArgumentException("you need define one type from script, http, ttl for check");
					}
					return res;
				}
	};
	
	public void registerService(SpecificEntity.Service service){
		Preconditions.checkNotNull(service, "service can't be null");
		logger.info("register service: " + service.getName() + "begin");
		AgentClient agentClient = consul.agentClient();
		Builder registration = ImmutableRegistration
				.builder()
				.name(service.getName())
				.port(Integer.valueOf(service.getPort()))
				.id(service.getId())
				.checks(Lists.transform(ForEachHelper.of(service.getChecks()), trans))
				.address(service.getAddress());
		if(service.getTags() != null) registration.addAllTags(service.getTags());
		agentClient.register(registration.build());
		logger.info("register service: " + service.getName() + "finished");
	}
	
	public void unregisterService(String serviceId){
		Preconditions.checkArgument(!Strings.isNullOrEmpty(serviceId), "service id can't be null or empty");
		AgentClient agentClient = consul.agentClient();
		agentClient.deregister(serviceId);
		logger.info("deregister service: " + serviceId);
	}
	
	public void unregisterServices(List<SpecificEntity.Service> services){
		for(SpecificEntity.Service service:ForEachHelper.of(services)){
			unregisterService(service.getId());
		}
	}
}

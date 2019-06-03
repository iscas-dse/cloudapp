package cn.ac.iscas.cloudapp.agent.entity.manager;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import cn.ac.iscas.cloudapp.agent.transaction.PropagationCoordinator;
import cn.ac.iscas.cloudapp.agent.transaction.PropagationEvent;

@Service
public class ConfigurationManager {
	@Autowired
	@Qualifier("propagationCoordinator")
	private PropagationCoordinator coordinator;
	
	public void alterConfig(String configKey, String configValue) throws IOException{
		PropagationEvent event = new PropagationEvent(configKey, configValue);
		coordinator.firePropagationEvent(event);
	}

	public void alterConfig(PropagationEvent proEvent) throws IOException {
		coordinator.firePropagationEvent(proEvent);
	}
}

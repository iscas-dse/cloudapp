package cn.ac.iscas.cloudapp.agent.transaction;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.kv.Value;
import cn.ac.iscas.cloudapp.agent.util.ForEachHelper;
import cn.ac.iscas.cloudapp.agent.util.KeyGenenater;

/**
 * find all listeners of one propagation event
 * @author RichardLcc
 */
@Component
public class PropagationListenerFactory {
	private static Logger logger = LoggerFactory.getLogger(PropagationListenerFactory.class);
	@Autowired
	private Consul consul;
	
	public List<PropagationListener> getListeners(PropagationEvent event){
		Preconditions.checkNotNull(event, "event can't be null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(event.getEventKey()), "propagation event's event key can't be null or empty");
		
		List<PropagationListener> listeners = new ArrayList<>();
		
		KeyValueClient keyValueClient = consul.keyValueClient();
		String key = KeyGenenater.genenateListenerKey(event.getEventKey());
		try{
			List<Value> values = keyValueClient.getValues(key);
			for(Value value: ForEachHelper.of(values)){
				//ip:port
				Optional<String> valueStr = value.getValueAsString();
				if(!valueStr.isPresent()) continue;
				logger.info(valueStr.get());
				String[] ipAndPort = valueStr.get().split(":");
				String addr = ipAndPort[0];
				int port = Integer.valueOf(ipAndPort[1]);
				PropagationListener listener = new PropagationListener(addr, port);
				logger.info("create a propagation listener({},{}) for event({})", addr, port, event.getEventKey());
				listeners.add(listener);
			}
		}catch(NotFoundException e){
			logger.info("no listeners for " + key);
		}
		return listeners;
	}
	
}

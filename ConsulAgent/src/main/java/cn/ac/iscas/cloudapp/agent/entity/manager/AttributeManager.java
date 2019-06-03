package cn.ac.iscas.cloudapp.agent.entity.manager;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;

import cn.ac.iscas.cloudapp.agent.util.ForEachHelper;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Attribute;
import jersey.repackaged.com.google.common.base.Preconditions;
@Service
public class AttributeManager {
	private static Logger logger = LoggerFactory.getLogger(AttributeManager.class);
	@Autowired
	private Consul consul;
	@Autowired
	private ConfigurationManager configManager;
	
	public void registerAttributes(SpecificEntity application, List<SpecificEntity.Attribute> attributes){
		String keyPrefix = generateKeyPrefix(application.getName());
		KeyValueClient client = consul.keyValueClient();
		for(Attribute attr : ForEachHelper.of(attributes)){
			client.putValue(keyPrefix + "/" + attr.getKey(), attr.getValue());
			try {
				configManager.alterConfig(attr.getKey(), attr.getValue());
			} catch (IOException e) {
				e.printStackTrace();
			}
			logger.info("register attribtue: " + attr.getKey() + ":" + attr.getValue());
		}
	}
	
	public void deleteAttributes(SpecificEntity application, List<SpecificEntity.Attribute> attributes){
		String keyPrefix = generateKeyPrefix(application.getName());
		KeyValueClient client = consul.keyValueClient();
		for(Attribute attr : ForEachHelper.of(attributes)){
			client.deleteKey(keyPrefix + "/" + attr.getKey());
			logger.info("delete attribtue: " + attr.getKey());
		}
	}
	
	private String generateKeyPrefix(String name){
		int index = name.lastIndexOf('_');
		Preconditions.checkArgument(index > 0, "application name invalid");
		return name.substring(0, index);
	}
}

package cn.ac.iscas.cloudapp.agent.entity.manager;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import cn.ac.iscas.cloudapp.agent.consul.ConsulServer;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Attribute;

@ActiveProfiles("AttributeManagerTest")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/application_context.xml"})
@Ignore
//目前存在问题，忽略该测试
public class AttributeManagerTest {
	
	private static ConsulServer consulServer;
	
	@BeforeClass
	public static void startServer() throws IOException{
		consulServer = new ConsulServer();
		consulServer.start();
	}
	
	@AfterClass
	public static void stopServer() throws IOException{
		consulServer.close();
	}
	
	@Autowired
	private AttributeManager attributeManager;
	
	@Autowired
	private Consul consul;
	
	@Test
	public void testRegisterAttributes() {
		List<Attribute> attributes = new ArrayList<>();
		Attribute attribute = new Attribute();
		attribute.setKey("fromAttributeManagerTest");
		attribute.setValue("fromAttributeManagerTest");
		attributes.add(attribute);
//		attributeManager.registerAttributes(attributes);
		KeyValueClient client = consul.keyValueClient();
		Optional<String> value = client.getValueAsString("fromAttributeManagerTest");
		assertNotNull(value);
		assertTrue(value.isPresent());
		assertEquals("fromAttributeManagerTest", value.get());
	}

	@Test
	public void testDeleteAttributes() {
		List<Attribute> attributes = new ArrayList<>();
		Attribute attribute = new Attribute();
		attribute.setKey("fromAttributeManagerTest");
		attribute.setValue("fromAttributeManagerTest");
		attributes.add(attribute);
//		attributeManager.deleteAttributes(attributes);
		KeyValueClient client = consul.keyValueClient();
		Optional<String> value = client.getValueAsString("fromAttributeManagerTest");
		assertNotNull(value);
		assertTrue(!value.isPresent());
	}
}

@Configuration
@Profile("AttributeManagerTest")
class PropagationCoordinatorTestConfig{
	
	@Bean
	@Primary
	public Consul consul(){
		return Consul.builder()
				.withHostAndPort(HostAndPort.fromParts("localhost", 8500)).build();
	}
}

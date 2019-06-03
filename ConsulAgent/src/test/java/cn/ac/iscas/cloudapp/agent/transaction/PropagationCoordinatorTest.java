package cn.ac.iscas.cloudapp.agent.transaction;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.orbitz.consul.Consul;
import com.orbitz.consul.EventClient;
import com.orbitz.consul.model.EventResponse;
import com.orbitz.consul.model.event.Event;

import cn.ac.iscas.cloudapp.agent.consul.ConsulServer;

@ActiveProfiles("PropagationCoordinatorTest")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/application_context.xml"})
public class PropagationCoordinatorTest {
	@Autowired
	@Qualifier("coordinator1")
	private PropagationCoordinator coordinator1;
	
	@Autowired
	@Qualifier("coordinator2")
	private PropagationCoordinator coordinator2;

//	private static ConsulServer consulServer;
	
	@Before
	public void init() throws IOException{
		coordinator1.start();
		coordinator2.start();
//		consulServer = new ConsulServer();
//		consulServer.start();
	}
	
//	@AfterClass
//	public static void stopServer() throws IOException{
//		consulServer.close();
//	}
	
	@Test
	public void firePropagationEventTest() throws IOException {	
		PropagationEvent event1 = new PropagationEvent("testKey", "value2");
		boolean result = coordinator1.firePropagationEvent(event1);
		assertEquals(true, result);
	}
}

@Configuration
@Profile("PropagationCoordinatorTest")
class PropagationCoordinatorTestConfig{
	@Bean
	@Primary
	public PropagationListenerFactory propagationListenerFactory() throws IOException{
		PropagationListenerFactory factory = Mockito.mock(PropagationListenerFactory.class);
		Mockito.when(factory.getListeners(Mockito.any(PropagationEvent.class))).then(new Answer<List<PropagationListener>>(){
			@Override
			public List<PropagationListener> answer(InvocationOnMock invocation) throws Throwable {
				List<PropagationListener> value = new ArrayList<>();
				value.add(new PropagationListener("localhost", 9003));
				value.add(new PropagationListener("133.133.134.163", 9001));
				return value;
			}
			
		});
		return factory;
	}
	@Bean
	@Primary
	public Consul consul(@Value("#{configs['consul_host']}") String host, @Value("#{configs['consul_port']}") int port) throws IOException{
		Consul consul = Mockito.mock(Consul.class);
		EventClient event = Mockito.mock(EventClient.class);
		Mockito.when(event.listEvents()).then(new Answer<EventResponse>() {
			@Override
			public EventResponse answer(InvocationOnMock invocation) throws Throwable {
				EventResponse response = new EventResponse() {
					@Override
					public BigInteger getIndex() {
						return new BigInteger("1111");
					}
					
					@Override
					public List<Event> getEvents() {
						return Collections.emptyList();
					}
				};
				return response;
			}
		});
		Mockito.when(consul.eventClient()).thenReturn(event);
		return consul;
	}
	
	@Bean(name="coordinator1")
	public PropagationCoordinator getCoordinator1(){
		return new PropagationCoordinator(9003);
	}
	
	@Bean(name="coordinator2")
	public PropagationCoordinator getCoordinator2(){
		return new PropagationCoordinator(9002);
	}
}
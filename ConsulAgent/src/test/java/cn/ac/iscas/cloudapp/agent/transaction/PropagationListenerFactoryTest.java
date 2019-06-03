package cn.ac.iscas.cloudapp.agent.transaction;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.EventClient;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.EventResponse;
import com.orbitz.consul.model.catalog.CatalogService;
import com.orbitz.consul.model.catalog.ImmutableCatalogService;
import com.orbitz.consul.model.event.Event;
import com.orbitz.consul.model.kv.ImmutableValue;
import com.orbitz.consul.model.kv.ImmutableValue.Builder;
import com.orbitz.consul.option.CatalogOptions;
@ActiveProfiles("PropagationListenerFactoryTest")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/application_context.xml")
public class PropagationListenerFactoryTest {

	@Autowired
	private PropagationListenerFactory factory;
	@Test
	public void testGetListeners() throws IOException {
		PropagationEvent event = new PropagationEvent("listener/serviceid/attribute/", "value");
		factory.getListeners(event);
	}
}

@Configuration
@Profile("PropagationListenerFactoryTest")
class PropagationListenerFactoryTestConfig{
	@SuppressWarnings("unchecked")
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
		
		CatalogClient catalogAgent = Mockito.mock(CatalogClient.class);
		KeyValueClient keyValueClient = Mockito.mock(KeyValueClient.class);
		
		Mockito.when(consul.catalogClient()).thenReturn(catalogAgent);
		Mockito.when(consul.keyValueClient()).thenReturn(keyValueClient);
		
		List<com.orbitz.consul.model.kv.Value> values = new ArrayList<>();
		Builder valueBuilder = ImmutableValue.builder();
		valueBuilder.createIndex(44).modifyIndex(65).lockIndex(0).key("listener/serviceid/attribute/listener1")
			.flags(0).value("eyJzZXJ2aWNlIjogInNlcnZpY2UiLCAic2VydmljZUlkIjogInNlcnZpY2VJZCIsICJ0YWciOiAic2VydmljZUlkIn0=");
		values.add(valueBuilder.build());
		
		valueBuilder.createIndex(43).modifyIndex(43).lockIndex(0).key("listener/serviceid/attribute/")
		.flags(0);
		values.add(valueBuilder.build());
		
		
		Mockito.when(catalogAgent.getService(Mockito.anyString(), Mockito.any(CatalogOptions.class)))
		.thenReturn(Mockito.mock(ConsulResponse.class,
			new Answer<List<CatalogService>>(){
				@Override
				public List<CatalogService> answer(InvocationOnMock invocation) throws Throwable {
					List<CatalogService> services = new ArrayList<>();
					ImmutableCatalogService.Builder builder = ImmutableCatalogService.builder();
					builder.serviceAddress("127.0.0.1")
						.servicePort(9001)
						.node("consul4")
						.address("localhost")
						.serviceName("listener")
						.serviceId("listener1");
					services.add(builder.build());
					return services;
				}
			}));
		
		
		Mockito.when(keyValueClient.getValues("listener/serviceid/attribute/")).thenReturn(values);
		
		return consul;
	}
}
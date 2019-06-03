package cn.ac.iscas.cloudapp.agent.config;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.EventClient;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.EventResponse;
import com.orbitz.consul.model.event.Event;

import cn.ac.iscas.cloudapp.agent.util.JsonUtils;

@Configuration
public class BeanConfiguration {
	@Bean
	public Consul consul(@Value("#{configs['consul_host']}") String host, @Value("#{configs['consul_port']}") int port) throws IOException{
		Consul consul = Mockito.mock(Consul.class);
		CatalogClient catalogAgent = Mockito.mock(CatalogClient.class);
		KeyValueClient keyValueClient = Mockito.mock(KeyValueClient.class);
		
		Mockito.when(consul.catalogClient()).thenReturn(catalogAgent);
		Mockito.when(consul.keyValueClient()).thenReturn(keyValueClient);
		
		String json = "[{\"CreateIndex\":44,"
				+ "\"ModifyIndex\":65,"
				+ "\"LockIndex\":0,"
				+ "\"Key\":\"listener/serviceid/attribute/listener1\","
				+ "\"Flags\":0,"
				+ "\"Value\":\"eyJzZXJ2aWNlIjogInNlcnZpY2UiLCAic2VydmljZUlkIjogInNlcnZpY2VJZCIsICJ0YWciOiAic2VydmljZUlkIn0=\"},"
				+ "{\"CreateIndex\":43,\"ModifyIndex\":43,\"LockIndex\":0,\"Key\":\"listener/serviceid/attribute/\",\"Flags\":0,\"Value\":null}]";
		List<com.orbitz.consul.model.kv.Value> value = JsonUtils.convertToObject(json , List.class);
		Mockito.when(keyValueClient.getValues("listener/serviceid/attribute/")).thenReturn(value );
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
}

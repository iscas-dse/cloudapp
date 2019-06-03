package consulAgent;
import static org.junit.Assert.*;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.EventClient;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.async.EventResponseCallback;
import com.orbitz.consul.model.EventResponse;
import com.orbitz.consul.model.event.Event;
import com.orbitz.consul.model.health.ServiceHealth;
import com.orbitz.consul.option.ImmutableEventOptions;
import com.orbitz.consul.option.ImmutableQueryOptions;
import com.orbitz.consul.option.QueryOptions;

import cn.ac.iscas.cloudapp.agent.consul.ConsulServer;

@Ignore
public class ConsulJavaAgentTest {
	
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
	
	@Test
	public void testJar(){
		Consul consul = Consul.newClient("localhost", 8500);
		KeyValueClient kvClient= consul.keyValueClient();
		kvClient.putValue("foo/bar","bar");
		String value = kvClient.getValueAsString("foo/bar").get();
		assertEquals("bar", value);
	}
	
	@Test
	public void testEventBlock(){
		Consul consul = Consul.newClient("localhost", 9000);
		EventClient eventClient = consul.eventClient();
		String eventId = "681b7365-62bb-0f54-dfc7-4428cfff64a9";
		String lower = eventId.substring(0, 8) + eventId.substring(9, 13)
		+ eventId.substring(14, 18);
		String upper = eventId.substring(19, 23) + eventId.substring(24, 36);
		BigInteger lowVal = new BigInteger(lower, 16);
		BigInteger highVal = new BigInteger(upper, 16);
		QueryOptions options = QueryOptions.blockSeconds(60, lowVal.xor(highVal)).build();
		eventClient.listEvents("event", options);
	}
	
	@Test
	public void testService(){
		Consul consul = Consul.newClient("localhost", 8500);
		HealthClient healthClient  = consul.healthClient();
		List<ServiceHealth> nodes = healthClient.getHealthyServiceInstances("Consul").getResponse();
		System.out.println(nodes);
	}
	
	@Test
	public void testRegisterService() throws NotRegisteredException{
		Consul consul = Consul.newClient("localhost", 8500);
		AgentClient agent = consul.agentClient();
		agent.register(9000, "netstat -ntlp | grep 8500", 10, "agent_register_test", "agent_register_1", "test");
	}
	
	@Test
	public void testUnregisterService(){
		Consul consul = Consul.newClient("localhost", 8500);
		AgentClient agent = consul.agentClient();
		agent.deregister("agent_register_1");
	}
	
	@Test
	public void testReceiveEvent(){
		Consul consul = Consul.newClient("localhost", 8500);
		EventClient eventClient = consul.eventClient();
		QueryOptions options = QueryOptions.blockSeconds(10, new BigInteger("10755669564693288449")).build();;
		EventResponse response = eventClient.listEvents("eventTest", options);
		for(Event event: response.getEvents()){
			System.out.println(event);
		}
		System.out.println(response.getIndex());
	}
	
	@Test
	public void testFireEvent(){
		Consul consul = Consul.newClient("localhost", 8500);
		EventClient eventClient = consul.eventClient();
		eventClient.fireEvent("messageEvent", null, "message");
	}
	
	@Test
	public void testFireEventWithNodeFilter(){
		Consul consul = Consul.newClient("133.133.135.221",8500);
		EventClient eventClient = consul.eventClient();
//		String filter = "consul\\d+";
//		System.out.println(filter);
//		filter.replace("\\\\", "\\");
//		System.out.println(filter);
//		EventOptions options = ImmutableEventOptions.builder().nodeFilter(filter).build();
//		eventClient.fireEvent("withNodeFilter2", options, "message");
		EventResponse response = eventClient.listEvents("deploy");
		for(Event event: response.getEvents()){
			System.out.println(event.getName());
			System.out.println(event.getNodeFilter());
			System.out.println(event.getLTime());
		}
	}
	
	@Test
	public void testEvent() throws InterruptedException{
		Consul consul = Consul.newClient("localhost", 8500);
		final EventClient eventClient = consul.eventClient();
		Runnable eventCon = new Runnable() {
			AtomicReference<BigInteger> gloablLastIndex = new AtomicReference<BigInteger>();
			public void run() {
				EventResponseCallback deployCallback = new EventResponseCallback() {
					@Override
					public void onFailure(Throwable throwable) {
						// TODO Auto-generated method stub
					}
					
					@Override
					public void onComplete(EventResponse eventResponse) {
						List<Event> events = eventResponse.getEvents();
						BigInteger lastIndex = eventResponse.getIndex();
						events = filterEvents(events, gloablLastIndex.get());
						for(Event event : events){
							String fileAddr = event.getPayload().get();
							System.out.println(fileAddr);
						}
						gloablLastIndex.set(lastIndex);
					}
					
					private BigInteger toIndex(String eventId){
						String lower = eventId.substring(0, 8) + eventId.substring(9, 13)
								+ eventId.substring(14, 18);
						String upper = eventId.substring(19, 23) + eventId.substring(24, 36);
						BigInteger lowVal = new BigInteger(lower, 16);
						BigInteger highVal = new BigInteger(upper, 16);
						return lowVal.xor(highVal);
					}
					
					protected List<Event> filterEvents(List<Event> toFilter, BigInteger lastIndex) {
						List<Event> events = toFilter;
						if (lastIndex != null) {
							for (int i = 0; i < events.size(); i++) {
								Event event = events.get(i);
								BigInteger eventIndex = toIndex(event.getId());
								if (eventIndex.equals(lastIndex)) {
									events = events.subList(i + 1, events.size());
									break;
								}
							}
						}
						return events;
					}
				};
				gloablLastIndex.set(new BigInteger("0"));
				while(true){
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(Thread.currentThread().getName() + "start receive event");
					ImmutableQueryOptions options = QueryOptions.blockSeconds(1, gloablLastIndex.get()).build();
					eventClient.listEvents("eventTest", options, deployCallback);
				}
			}
		};
		Runnable fireEvent = new Runnable() {
			@Override
			public void run() {
				//every two seconds fire a event
				int index = 0;
				while(true){
					try {
						index++;
						ImmutableEventOptions options = ImmutableEventOptions.builder().build();
						System.out.println(Thread.currentThread().getName() + "start fire event");
						eventClient.fireEvent("eventTest", options, "message" + index);
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		Thread fire = new Thread(fireEvent);
		Thread consumer = new Thread(eventCon);
		fire.start();consumer.start();
		fire.join();consumer.join();
	}
}

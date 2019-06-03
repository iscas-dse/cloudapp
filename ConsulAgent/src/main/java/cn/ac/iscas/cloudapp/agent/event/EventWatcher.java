package cn.ac.iscas.cloudapp.agent.event;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orbitz.consul.Consul;
import com.orbitz.consul.EventClient;
import com.orbitz.consul.model.EventResponse;
import com.orbitz.consul.model.event.Event;
import com.orbitz.consul.option.ImmutableQueryOptions;
import com.orbitz.consul.option.QueryOptions;

/**
 * listen consul cluster's event
 * @author RichardLcc
 *
 */
@Component
public class EventWatcher{
	@Autowired
	private Consul consul;
	
	private EventClient eventClient;
	private AtomicReference<BigInteger> lastIndex = new AtomicReference<BigInteger>();
	
	@PostConstruct
	public void initEventWatcher() {
		eventClient =  consul.eventClient();
		BigInteger index = eventClient.listEvents().getIndex();
		lastIndex.set(index);
	}
	
	public List<Event> watch(){
		QueryOptions queryOptions = ImmutableQueryOptions.builder()
				.wait("1m0s").index(lastIndex.get()).build();
		EventResponse response = eventClient.listEvents(queryOptions);
		List<Event> events = filterEvents(response.getEvents(), lastIndex.get());
		lastIndex.set(response.getIndex());
		return events;
	}
	
	public List<Event> watch(String eventName){
		ImmutableQueryOptions queryOptions = QueryOptions.blockSeconds(60, lastIndex.get()).build();;
		EventResponse response = eventClient.listEvents(eventName, queryOptions);
		List<Event> events = filterEvents(response.getEvents(), lastIndex.get());
		lastIndex.set(response.getIndex());
		return events;
	}
	
	/**
	 * @description convert eventId to Index,
	 * @param eventId
	 * @return BigInteger
	 * 2016年6月24日 下午4:43:35
	 */
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
}
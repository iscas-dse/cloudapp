package cn.ac.iscas.cloudapp.agent.event;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.ac.iscas.cloudapp.agent.ApplicationManager;
import cn.ac.iscas.cloudapp.agent.ThreadPoolManager;
import cn.ac.iscas.cloudapp.agent.entity.manager.ConfigurationManager;
import cn.ac.iscas.cloudapp.agent.transaction.PropagationEvent;
import cn.ac.iscas.cloudapp.agent.util.JsonUtils;
import net.lingala.zip4j.exception.ZipException;

import com.orbitz.consul.model.event.Event;

/**
 * register to listen event from event watcher, and handle it.
 * you can register event handler using function addEventListener(String EventName, Function)
 * @author RichardLcc
 *
 */
@Component
public class EventWorker extends Thread{
	private static Logger logger = LoggerFactory.getLogger(EventWorker.class);
	
	private long interval;
	private volatile boolean running = true;
	
	private Map<String, CallbackFunction> maps;
	@Autowired
	private EventWatcher watcher;
	@Autowired
	private ApplicationManager appManager;
	@Autowired
	private ConfigurationManager configManager;
	@Autowired
	private ThreadPoolManager threadPool;
	
	@Autowired
	public EventWorker(@Value("#{configs['agent.interval']}") long interval) {
		logger.info("init executor's interval: " + interval);
		this.interval = interval;
		maps = new HashMap<String, EventWorker.CallbackFunction>();
	}
	
	private void addEventListener(String eventName, CallbackFunction callback){
		maps.put(eventName, callback);
	}
	
	@PostConstruct
	private void addDeleteCallback() {
		CallbackFunction deleteCallback = new CallbackFunction() {
			@Override
			public void call(Event event) {
				String appName = event.getPayload().get();
				logger.info("delete event:" + appName);
				appManager.deleteApp(appName);
			}
		};
		this.addEventListener("delete", deleteCallback);
		this.addEventListener("DELETE", deleteCallback);
	}
	
	@PostConstruct
	private void addStartCallback(){
		CallbackFunction startCallback = new CallbackFunction() {
			@Override
			public void call(Event event) {
				String appName = event.getPayload().get();
				logger.info("start event: " + appName);
				appManager.startApp(appName);
			}
		};
		this.addEventListener("start", startCallback);
		this.addEventListener("START", startCallback);
	}
	@PostConstruct
	private void addStopCallback(){
		CallbackFunction stopCallbackFunction = new CallbackFunction() {
			@Override
			public void call(Event event) {
				String appName = event.getPayload().get();
				logger.info("stop event: " + appName);
				appManager.stopApp(appName);
			}
		};
		this.addEventListener("stop", stopCallbackFunction);
		this.addEventListener("STOP", stopCallbackFunction);
	}
	
	@PostConstruct
	private void addDeployCallback(){
		CallbackFunction deployCallback = new CallbackFunction() {
			@Override
			public void call(Event event) {
				//TODO 创建app
				String url = event.getPayload().get();
				try {
					appManager.createApp(url);
				} catch (IOException | ZipException e) {
					e.printStackTrace();
				}catch(RuntimeException e){
					e.printStackTrace();
				}
			}
		};
		this.addEventListener("deploy", deployCallback);
		this.addEventListener("DEPLOY", deployCallback);
	}
	
	@PostConstruct
	private void addConfigEventCallback(){
		CallbackFunction configEventCallback = new CallbackFunction(){
			public void call(Event event) {
				String configEvent = event.getPayload().get();
				logger.info("config event payload: {}", configEvent);
				PropagationEvent proEvent;
				try {
					proEvent = JsonUtils.convertToObject(configEvent, PropagationEvent.class);
					configManager.alterConfig(proEvent);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		this.addEventListener("config", configEventCallback);
		this.addEventListener("CONFIG", configEventCallback);
	}
	
	@Override
	public void run() {
		while(running){
			List<Event> events = watcher.watch();
			for(Event event : events){
				logger.info("get a event: " + event.getName());
				Runnable task = createTask(event);
				threadPool.getPool().submit(task);
			}
		}
	}

	private Runnable createTask(final Event event) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				CallbackFunction func = maps.get(event.getName());
				if(func != null){
					func.call(event);
				}
			}
		};
		return task;
	}
	
	public void shutdown(){
		running = false;
	}
	
	private interface CallbackFunction {
		public void call(Event event);
	}
}

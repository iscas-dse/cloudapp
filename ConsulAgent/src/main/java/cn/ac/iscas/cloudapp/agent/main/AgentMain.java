package cn.ac.iscas.cloudapp.agent.main;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import cn.ac.iscas.cloudapp.agent.event.EventWorker;

public class AgentMain {
	public static void main(String[] args) {
		System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "prod");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"application_context.xml");
		EventWorker executor = (EventWorker) context.getBean("eventWorker");
		executor.start();
		try {
			executor.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			context.close();
		}
	}
}

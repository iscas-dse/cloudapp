package cn.ac.iscas.cloudapp.agent.entity.manager;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.catalog.CatalogService;
import cn.ac.iscas.cloudapp.agent.consul.ConsulServer;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Check;
import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Service;

@ActiveProfiles("ServiceManagerTest")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/application_context.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Ignore
public class ServiceManagerTest {
	
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
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Autowired
	private ServiceManager manager;
	
	@Autowired
	private Consul consul;
	
	@Test
	public void order1_testRegisterService() {
		Service service= Service.builder()
			.address("133.133.133.48")
			.id("ServiceManagerTest1")
			.name("ServiceManagerTest")
			.port("8500")
			.check(Check.builder().interval(10L).http("").build())
			.build();
		manager.registerService(service);
		List<CatalogService> services = consul.catalogClient()
				.getService("ServiceManagerTest").getResponse();
		assertEquals(1, services.size());
		CatalogService factService = services.get(0);
		assertEquals("ServiceManagerTest1", factService.getServiceId());
		assertEquals("133.133.133.48", factService.getServiceAddress());
		assertEquals(8500,factService.getServicePort());
	}
	
	@Test
	public void order2_testRegisterServiceWithWrongCheck(){
		Service service= new Service();
		List<Check> checks = new ArrayList<>();
		Check check = new Check();
		checks.add(check);
		service.setChecks(checks);
		service.setAddress("133.133.133.48");
		service.setId("ServiceManagerTest2");
		service.setName("ServiceManagerTest");
		service.setPort("8600");
		exception.expect(NullPointerException.class);
		exception.expectMessage("check interval can't be null");
		manager.registerService(service);
	}
	
	@Test
	public void order2_testRegisterService(){
		Service service= new Service();
		List<Check> checks = new ArrayList<>();
		Check check = new Check();
		check.setInterval(10L);
		check.setScript("script");
		checks.add(check);
		service.setChecks(checks);
		service.setAddress("133.133.133.48");
		service.setId("ServiceManagerTest2");
		service.setName("ServiceManagerTest");
		service.setPort("8600");
		manager.registerService(service);
		List<CatalogService> services = consul.catalogClient()
				.getService("ServiceManagerTest").getResponse();
		assertEquals(2, services.size());
		CatalogService factService = services.get(1);
		assertEquals("ServiceManagerTest2", factService.getServiceId());
		assertEquals("133.133.133.48", factService.getServiceAddress());
		assertEquals(8600,factService.getServicePort());
	}
	
	@Test
	public void order2_testNullService(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("service can't be null");
		manager.registerService(null);
	}

	@Test
	public void order3_testUnregisterService() {
		manager.unregisterService("ServiceManagerTest1");
		List<CatalogService> services = consul.catalogClient()
				.getService("ServiceManagerTest1").getResponse();
		assertEquals(0, services.size());
	}

	@Test
	public void order4_testUnregisterNullServices() {
		manager.unregisterServices(null);
	}
	
	@Test
	public void order5_testUnRegisterServices(){
		Service service1 = new Service();
		service1.setId("ServiceManagerTest1");
		Service service2 = new Service();
		service2.setId("ServiceManagerTest2");
		List<Service> services = Lists.newArrayList(service1,service2);
		manager.unregisterServices(services);
		List<CatalogService> factservices = consul.catalogClient()
				.getService("ServiceManagerTest").getResponse();
		assertEquals(0, factservices.size());
	}
	
}

@Configuration
@Profile("ServiceManagerTest")
class ServiceManagerTestConfig{
	
	@Bean
	@Primary
	public Consul consul(){
		return Consul.builder()
				.withHostAndPort(HostAndPort.fromParts("localhost", 8500)).build();
	}
}

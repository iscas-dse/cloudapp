package cn.ac.iscas.cloudapp.agent.consul;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Ignore
public class ConsulTemplateServerTest {
	
	private static ConsulTemplateServer server;
	
	@BeforeClass
	public static void initServer(){
		server = new ConsulTemplateServer();
	}
	
	@Test
	public void test1_Start() throws IOException {
		server.start();
	}
	
	@Test
	public void test2_Reload() throws IOException{
		server.reload();
	}

	@Test
	public void test3_Close() throws IOException {
		server.close();
	}

}

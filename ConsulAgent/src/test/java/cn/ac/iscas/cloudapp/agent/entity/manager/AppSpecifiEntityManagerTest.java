package cn.ac.iscas.cloudapp.agent.entity.manager;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("AppSpecifiEntityManagerTest")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/application_context.xml"})
public class AppSpecifiEntityManagerTest {

	@Test
	public void testStore() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGet() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testDeleteSpecificEntity() {
		fail("Not yet implemented"); // TODO
	}

}


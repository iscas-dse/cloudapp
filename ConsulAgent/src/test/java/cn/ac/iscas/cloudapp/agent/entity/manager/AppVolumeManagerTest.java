package cn.ac.iscas.cloudapp.agent.entity.manager;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity.Template;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/application_context.xml"})
public class AppVolumeManagerTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Autowired
	private AppVolumeManager volumeManager;
	
	@Test
	public void testCreateVolumeFileWithWrongTemplate() {
		String appName = "testAppFromAppVolumeManagerTest";
		Template template = new Template();
		exception.expect(IllegalArgumentException.class);
		volumeManager.createVolumeFile(appName, template);
	}
}

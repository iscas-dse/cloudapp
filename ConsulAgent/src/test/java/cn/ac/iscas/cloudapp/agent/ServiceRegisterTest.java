package cn.ac.iscas.cloudapp.agent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import com.google.common.base.Charsets;

import cn.ac.iscas.cloudeploy.v2.packet.entity.SpecificEntity;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/application_context.xml")
@Ignore
public class ServiceRegisterTest {
	@Autowired
	private AppCreater register;
	@Test
	public void testRegister() throws IOException {
		Yaml yaml = new Yaml();
		Path file = Paths.get("src/test/resources/configuration/test.yaml");
		SpecificEntity entity = yaml .loadAs(
				Files.newBufferedReader(file,Charsets.UTF_8), SpecificEntity.class);
		register.register(entity);
	}
}

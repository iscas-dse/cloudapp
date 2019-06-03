package cn.ac.iscas.cloudeploy.v2.packet.builder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.ac.iscas.cloudeploy.v2.model.entity.topology.ServiceTemplate;
import cn.ac.iscas.cloudeploy.v2.packet.builder.puppet.VMPuppetPacketBuilder;
import cn.ac.iscas.cloudeploy.v2.util.K8sUtil;
import cn.ac.iscas.cloudeploy.v2.util.YamlUtils;
import cn.ac.iscas.cloudeploy.v2.workflowEngine.entity.Deployment;


public class VMPuppetPacketBuilderTest {
	@Test
	public void testBuildDeployment() throws IOException {
		K8sUtil.getNameSpace();
	}
}

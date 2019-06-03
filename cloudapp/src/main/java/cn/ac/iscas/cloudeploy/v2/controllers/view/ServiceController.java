package cn.ac.iscas.cloudeploy.v2.controllers.view;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import cn.ac.iscas.cloudeploy.v2.util.ConstantsUtil;

@Controller
@RequestMapping(value = "v2/views/service")
public class ServiceController {
	/**
	 * 默认的主页面
	 * http://localhost:8080/cloudapp/
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String mainPage() {
		return "app/main";
	}
	/**
	 * 应用列表界面
	 * http://localhost:8080/cloudapp/v2/views/applications/list
	 * @return
	 */
	@RequestMapping(value = { "/list" }, method = RequestMethod.GET)
	public String listPage() {
		return "app/list";
	}

	/**
	 * 模型编排界面
	 * http://localhost:8080/cloudapp/v2/views/applications/panel
	 * @return
	 */
	@RequestMapping(value = { "/panel" }, method = RequestMethod.GET)
	public String panelPage() {
		return "app/panel";
	}
	@RequestMapping(value = { "/docker" }, method = RequestMethod.GET)
	public ModelAndView dockerPage() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("url", ConstantsUtil.getConstant().DOCKER_URL);
		return new ModelAndView("service/docker", map);
	}
	@RequestMapping(value = { "/cluster" }, method = RequestMethod.GET)
	public ModelAndView clusterPage() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("url", ConstantsUtil.getConstant().K8S_DASHBOARD_URL);
		return new ModelAndView("service/cluster", map);
	}
	@RequestMapping(value = { "/consul" }, method = RequestMethod.GET)
	public ModelAndView consulPage() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("url", ConstantsUtil.getConstant().CONSUL_URL);
		return new ModelAndView("service/consul", map);
		
	}
	@RequestMapping(value = { "/weave" }, method = RequestMethod.GET)
	public ModelAndView weavePage() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("url", ConstantsUtil.getConstant().WEAVE_URL);
		return new ModelAndView("service/weave", map);
	
	}
}

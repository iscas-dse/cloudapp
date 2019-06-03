package cn.ac.iscas.cloudeploy.v2.controllers.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 视图页面跳转控制
 * @author Simon Lee
 *
 */
@Controller
@RequestMapping(value = "v2/views/applications")
//@Transactional
public class ApplicationViewController {
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
}

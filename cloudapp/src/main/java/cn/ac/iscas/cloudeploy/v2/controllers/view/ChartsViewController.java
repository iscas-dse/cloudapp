package cn.ac.iscas.cloudeploy.v2.controllers.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "v2/views/charts")

public class ChartsViewController {
	@RequestMapping(method = RequestMethod.GET)
	public String mainPage() {
		return "charts/charts";
	}
}

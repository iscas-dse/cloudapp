package cn.ac.iscas.cloudeploy.v2.controllers.view;

import javax.transaction.Transactional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/login")
public class LoginController {

  //  @Autowired
   // UserService userService;

    /**
     * 新建用户
     * */
    /*@RequestMapping(value = {"/register"}, method = RequestMethod.POST)
    public UserView.Item createUser(@RequestBody UserView.Item userItem){
        return UserView.detailedViewOf(userService.createUser(userItem));
    }*/

    @RequestMapping(method = RequestMethod.GET)
	public String mainPage() {
		return "login/login";
	}
}

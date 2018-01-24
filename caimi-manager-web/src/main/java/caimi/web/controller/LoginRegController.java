package caimi.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import caimi.web.Service.auth.UserService;
import caimi.web.beans.RespBean;
import caimi.web.beans.User;

@RestController
public class LoginRegController {

	@Autowired
	private UserService userService;
	
	@RequestMapping("/login_error")
	public RespBean loginError() {
		return new RespBean("error","login failed!");
	}
	
	@RequestMapping("/login_success")
	public RespBean loginSuccesss() {
		return new RespBean("success","welcome to caimi!!");
	}
	
	@RequestMapping("/login_page")
	public RespBean loginPage() {
		return new RespBean("error","need to regist or login!");
	}
	
	/*
	 * 注册时传入一个表单
	 */
	@RequestMapping("/regist")
	public RespBean regist(User user) {
		int result = userService.register(user);
		if(result == 0) {
			//success
			return new RespBean("success","welcome to caimi!");
		}else if (result == 1) {
			return new RespBean("error","change your username!");
		}else {
			return new RespBean("error","need to regist or login!");
		}
	}
}

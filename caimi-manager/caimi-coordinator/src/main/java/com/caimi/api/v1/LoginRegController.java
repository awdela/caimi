package com.caimi.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caimi.service.auth.UserService;
import com.caimi.service.beans.RespBean;

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
	 * ע��ʱ����һ����
	 */
//	@RequestMapping("/regist")
//	public RespBean regist(UserEntity user) {
//		int result = userService.reg(user);
//		if(result == 0) {
//			//success
//			return new RespBean("success","welcome to caimi!");
//		}else if (result == 1) {
//			return new RespBean("error","change your username!");
//		}else {
//			return new RespBean("error","need to regist or login!");
//		}
//	}
//	
//	/**
//	 * test
//	 */
//    @RequestMapping(value = "/getUserName",method = RequestMethod.GET)
//    public UserEntity getUserName(String id) {
//    	UserEntity userEntity = userService.getUserById(id);
//        return userEntity;
//    }
}

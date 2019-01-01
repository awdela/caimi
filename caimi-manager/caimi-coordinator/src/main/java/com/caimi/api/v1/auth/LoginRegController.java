package com.caimi.api.v1.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.caimi.api.v1.RestControllerConstants;
import com.caimi.service.auth.UserService;
import com.caimi.service.repository.entity.UserEntity;
import com.caimi.util.beans.RespBean;

@RestController
public class LoginRegController {

    private static final Logger logger = LoggerFactory.getLogger(LoginRegController.class);

    private static final String URL_PREFIX = RestControllerConstants.URI_AUTH;

    @Autowired
    private UserService userService;

    @RequestMapping("/login_error")
    public RespBean loginError() {
        return new RespBean("error", "login failed!");
    }

    @RequestMapping("/login_success")
    public RespBean loginSuccesss() {
        return new RespBean("success", "welcome to caimi!!");
    }

    @RequestMapping("/login_page")
    public RespBean loginPage() {
        return new RespBean("error", "need to regist or login!");
    }

    /*
     * regist user
     */
    @RequestMapping(path = URL_PREFIX
            + "/regist", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public RespBean regist(@RequestBody UserEntity user) {
        int result = userService.regist(user);
        if (result == 0) {
            // success
            return new RespBean("success", "welcome to caimi!");
        } else if (result == 1) {
            return new RespBean("error", "change your username!");
        } else {
            return new RespBean("error", "need to regist or login!");
        }
    }

    // login
    @RequestMapping(path = URL_PREFIX
            + "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public RespBean login(@RequestBody UserEntity user) {
        int result = userService.login(user);
        if (result == 0) {
            // success
            return new RespBean("success", "welcome to caimi!");
        } else if (result == 1) {
            return new RespBean("error", "change your username!");
        } else {
            return new RespBean("error", "need to regist or login!");
        }
    }

}

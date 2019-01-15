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

    @RequestMapping(path = URL_PREFIX
            + "/info", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public RespBean info(@RequestBody String id) {
        UserEntity user = (UserEntity) userService.getUserById(id);
        if (user != null) {
            // success
            return new RespBean("success", user);
        } else {
            return new RespBean("error", "need to regist or login!");
        }
    }

    /*
     * regist user
     */
    @RequestMapping(path = URL_PREFIX
            + "/regist", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public RespBean regist(@RequestBody UserEntity user) {
        return userService.regist(user);
    }

    // login
    @RequestMapping(path = URL_PREFIX
            + "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public RespBean login(@RequestBody UserEntity user) {
        return userService.login(user);

    }

    @RequestMapping(path = URL_PREFIX
            + "/logout", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public RespBean logout(@RequestBody UserEntity user) {
        return userService.deleteUser(user);

    }

    @RequestMapping(path = URL_PREFIX
            + "/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public RespBean update(@RequestBody UserEntity user) {
        return userService.updateUser(user);
    }

}

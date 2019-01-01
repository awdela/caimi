package com.caimi.api.v1.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.caimi.service.repository.SpringJPABORepository;
import com.caimi.util.beans.RespBean;

@RestController
public class RepositoryController2 {
	
	@Autowired
	private SpringJPABORepository jpa;
	
	// login
    @RequestMapping(path = "/repository/init", 
    		method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public RespBean init() {
    	jpa.initEntityClasses();
    	return new RespBean("success", "init repository");
    }

}

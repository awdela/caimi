package com.caimi.service.dao;

import com.caimi.service.repository.entity.User;

public interface UserDao {

	User getById(String id);
	
	User getByName(String userName);
	
	Integer save(User user);
	
	Integer delete(String id);
	
}

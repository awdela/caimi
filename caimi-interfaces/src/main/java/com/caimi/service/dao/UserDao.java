package com.caimi.service.dao;

import com.caimi.service.repository.entity.Role;
import com.caimi.service.repository.entity.User;

public interface UserDao {

	User getById(String id);
	
	User getByName(String userName);
	
	void save(User user);
	
	void delete(String id);
	
	Role getRole(String id);
	
}

package com.caimi.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caimi.service.BORepository;
import com.caimi.service.dao.UserDao;
import com.caimi.service.repository.entity.User;

@Service
public class UserDaoImpl implements UserDao{

	@Autowired
	private BORepository repository;
	
	@Override
	public User getById(String id) {
		return null;
	}

	@Override
	public User getByName(String userName) {
		return null;
	}

	@Override
	public Integer save(User user) {
		return null;
	}

	@Override
	public Integer delete(String id) {
		return null;
	}

}

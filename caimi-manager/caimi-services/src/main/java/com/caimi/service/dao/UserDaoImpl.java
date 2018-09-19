package com.caimi.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caimi.service.repository.BOEntity;
import com.caimi.service.repository.BORepository;
import com.caimi.service.repository.entity.AbstractBusinessEntity;
import com.caimi.service.repository.entity.Role;
import com.caimi.service.repository.entity.User;
import com.caimi.service.repository.entity.UserEntity;

@Service
public class UserDaoImpl implements UserDao{

	@Autowired
	private BORepository repository;
	@Autowired
	private RoleUserDao roleUserDao;
	
	@Override
	public User getById(String id) {
		UserEntity entity = (UserEntity) repository.getIdBy(UserEntity.class, BOEntity.KEY_ID, id);
		return entity;
	}

	@Override
	public User getByName(String userName) {
		UserEntity entity = (UserEntity) repository.getIdBy(UserEntity.class, AbstractBusinessEntity.KEY_NAME, userName);
		return entity;
	}

	@Override
	public void save(User user) {
		repository.save(user);
	}

	@Override
	public void delete(String id) {
		repository.detectBOClassById(id);
	}

	@Override
	public Role getRole(String id) {
		return roleUserDao.getRole(id);
	}
	
}
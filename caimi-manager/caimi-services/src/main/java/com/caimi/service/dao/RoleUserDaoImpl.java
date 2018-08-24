package com.caimi.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caimi.service.repository.BOEntity;
import com.caimi.service.repository.BORepository;
import com.caimi.service.repository.entity.Role;
import com.caimi.service.repository.entity.RoleUser;

@Service
public class RoleUserDaoImpl implements RoleUserDao{

	@Autowired
	private BORepository repository;
	
	@Override
	public Role getRole(String roleId) {
		Role role = (Role) repository.getIdBy(Role.class, BOEntity.KEY_ID, roleId);
		return role;
	}

	@Override
	public void saveRU(RoleUser ru) {
		repository.save(ru);
	}


}

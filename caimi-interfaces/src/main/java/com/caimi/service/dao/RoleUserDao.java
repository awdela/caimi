package com.caimi.service.dao;

import com.caimi.service.repository.entity.Role;
import com.caimi.service.repository.entity.RoleUser;

public interface RoleUserDao {
	
	public Role getRole(String roleId);

	public void saveRU(RoleUser ru);
	
}

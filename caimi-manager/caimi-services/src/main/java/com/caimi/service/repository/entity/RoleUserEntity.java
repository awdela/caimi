package com.caimi.service.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "role_user")
public class RoleUserEntity implements RoleUser{

	@Column(name = "role_user_id")
	private String roleUserId;
	
	@Column(name = "role_id")
	private int roleId;
	
	@Column(name = "user_id")
	private String userId;

	public String getRoleUserId() {
		return roleUserId;
	}

	public void setRoleUserId(String roleUserId) {
		this.roleUserId = roleUserId;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}

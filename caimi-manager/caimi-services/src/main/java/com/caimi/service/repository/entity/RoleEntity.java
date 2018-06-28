package com.caimi.service.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "role")
public class RoleEntity implements Role, Comparable<RoleEntity>{

	@Column(name = "role_id")
	private int roleId;
	
	@Column(name = "role_name")
	private String roleName;
	
	@Column(name = "role_level")
	private int roleLevel;
	
	@Column(name = "role_desc")
	private String roleDesc;
	
	@Column(name = "menu_item")
	private String menuItem;

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public int getRoleLevel() {
		return roleLevel;
	}

	public void setRoleLevel(int roleLevel) {
		this.roleLevel = roleLevel;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public String getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(String menuItem) {
		this.menuItem = menuItem;
	}

	@Override
	public int compareTo(RoleEntity o) {
		if (roleId == o.getRoleId()) {
			return 0;
		}else if (roleId > o.getRoleId()) {
			return 1;
		}else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return super.toString();
	}
	
}

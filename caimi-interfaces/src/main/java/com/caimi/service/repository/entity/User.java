package com.caimi.service.repository.entity;

import com.caimi.service.repository.BusinessEntity;

public interface User extends BusinessEntity{

    public String getPassword();

	public void setPassword(String passWord);

	public String getDepartmentId();

	public String getTitle();

	public String getEmail();

	public String getPhone();

    public Role getRole();

    public void setRole(Role role);

}

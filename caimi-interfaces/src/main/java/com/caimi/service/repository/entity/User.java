package com.caimi.service.repository.entity;

import com.caimi.service.repository.BusinessEntity;

public interface User extends BusinessEntity{
	
	public void setPassword(String passWord);
	
	public String getPassword();
	
	public String getDepartmentId();
	
	public String getTitle();
	
	public String getEmail();
	
	public String getPhone();
	
}

package com.caimi.service.repository.entity;

public interface User extends BusinessEntity{

    public String getPassword();

	public void setPassword(String passWord);

	public String getTitle();

	public String getEmail();

	public String getPhone();

    public String getRole();

    public void setRole(String role);
    
}

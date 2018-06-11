package com.caimi.service.repository.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.caimi.service.repository.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserEntity implements User, UserDetails{

	private static final long serialVersionUID = 1L;
	private String id;
	private String username;
	private String password;
	private boolean enable;
	private String email;
	private String userface;
	private Timestamp regTime;
	
	@JsonIgnore
	public Collection<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//		for()
		return null;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserface() {
		return userface;
	}

	public void setUserface(String userface) {
		this.userface = userface;
	}

	public Timestamp getRegTime() {
		return regTime;
	}

	public void setRegTime(Timestamp regTime) {
		this.regTime = regTime;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public boolean isAccountNonExpired() {
		return false;
	}

	public boolean isAccountNonLocked() {
		return false;
	}

	public boolean isCredentialsNonExpired() {
		return false;
	}

	public boolean isEnabled() {

		return false;
	}

	public LocalDateTime getCreationTime() {
		return null;
	}

	public void setCreationTime(LocalDateTime time) {
		
	}

	public LocalDateTime getLastActivityTime() {
		return null;
	}

	public LocalDateTime touchActivityTime() {

		return null;
	}

}

package com.caimi.service.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caimi.service.repository.entity.UserEntity;

@Service
@Transactional
public class UserService implements UserDetailsService{

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * spring boot security
	 */
	
	/**
	 * @param user
	 * @return 0 success
	 * @return 1 repeat username
	 * @return 2 failed
	 */
//	public int reg(UserEntity user) {
//		UserEntity repeatUserName = userMapper.loadUserByUserNmae(user.getUsername());
//		if(repeatUserName!=null) {
//			return 1;
//		}
//		//Digest the passwd
//		user.setEnable(true);
//		user.setPassword(StringUtil.md5(user.getPassword()));
//		long result = userMapper.reg(user);
//		if(result == 1) {
//			return 0;
//		}else {
//			return 2;
//		}
//	}
//
//	/**
//	 * select
//	 */
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		UserEntity user = userMapper.loadUserByUserNmae(username);
//		if(null != user) {
//			return user;
//		}else {
//			return new UserEntity();
//		}
//	}
//	
//	public int updateUserEmail(String email, String id) {
//		return userMapper.updateUserEmail(email, id);
//	}
//	
//	public int updateUserEnabled(Boolean enabled, Long uid) {
//		return userMapper.updateUserEnabled(enabled, uid);
//	}
//	
//	public int deleteUserById(long uid) {
//		return userMapper.deleteUserById(uid);
//	}
//	
//	public UserEntity getUserById(@Param("id") String id) {
//		return userMapper.getUserById(id);
//	}
//	
}

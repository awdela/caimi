package com.caimi.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caimi.service.dao.UserDao;
import com.caimi.service.repository.entity.User;
import com.caimi.service.repository.entity.cache.UserEntity;
import com.caimi.util.StringUtil;

@Service
@Transactional
public class UserService implements UserDetailsService{

	@Autowired
	private UserDao userDao;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
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
	public int reg(UserEntity user) {
		UserEntity repeatUserName = (UserEntity) userDao.getByName(user.getName());
		if(repeatUserName!=null) {
			return 1;
		}
		//Digest the passwd
		user.setPassWord(StringUtil.md5(user.getPassword()));
		userDao.save(user);
		return 0;
	}

	public int deleteUserById(String uid) {
		userDao.delete(uid);
		return 0;
	}
	
	public User getUserById(@Param("id") String id) {
		return userDao.getById(id);
	}
	
	public User getUserByName(@Param("name") String name) {
		return userDao.getByName(name);
	}
	
}

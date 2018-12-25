package com.caimi.service.auth;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caimi.service.dao.UserDao;
import com.caimi.service.repository.entity.Role;
import com.caimi.service.repository.entity.User;
import com.caimi.service.repository.entity.UserEntity;
import com.caimi.util.StringUtil;
import com.caimi.util.UUIDUtil;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService{

	@Autowired
	private UserDao userDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = (UserEntity) userDao.getByName(username);
		if (user == null) {
			throw new UsernameNotFoundException("用户名不存在");
		}
		Role role = userDao.getRole(user.getId());
		//用于添加用户的权限,只要把用户权限添加到authorities
		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
		return user;
	}

	/**
	 * @param user
	 * @return 0 success
	 * @return 1 username not exist
	 * @return 2 failed
	 */
	@SuppressWarnings("null")
	@Override
	public int login(User user) {
		User userEntity = userDao.getByName(user.getName());
		if(userEntity!=null) {
			return 1;
		}
		if (userEntity.getPassword().equals(user.getPassword())) {
			return 0;
		}
		return 2;
	}

	/**
	 * @param user
	 * @return 0 success
	 * @return 1 repeat username
	 * @return 2 failed
	 */
	@Override
    public int regist(User user) {
		User repeatUserName = userDao.getByName(user.getName());
		if(repeatUserName!=null) {
			return 1;
		}
		//Digest the passwd
        user.setId(ID_PREFIX_USER + UUIDUtil.genId());
		user.setPassword(StringUtil.md5(user.getPassword()));
		userDao.save(user);
		return 0;
	}

	@Override
	public int deleteUserById(String uid) {
		userDao.delete(uid);
		return 0;
	}

	@Override
    public User getUserById(@Param("id") String id) {
		return userDao.getById(id);
	}

	@Override
    public User getUserByName(@Param("name") String name) {
		return userDao.getByName(name);
	}

}

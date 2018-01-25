package caimi.web.service.auth;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import caimi.web.beans.User;
import caimi.web.mapper.UserMapper;

@Service
@Transactional
public class UserService implements UserDetailsService{
	
	/*
	 * spring boot security
	 */
	@Autowired
	private UserMapper userMapper;
	
	/**
	 * @param user
	 * @return 0 success
	 * @return 1 repeat username
	 * @return 2 failed
	 */
	public int register(User user) {
		User repeatUserName = userMapper.loadUserByUserNmae(user.getUsername());
		if(repeatUserName!=null) {
			return 1;
		}
		//Digest the passwd
		user.setEnable(true);
		user.setPassword(DigestUtils.md5(user.getPassword().getBytes()).toString());
		long result = userMapper.reg(user);
		if(result == 1) {
			return 0;
		}else {
			return 2;
		}
	}

	/**
	 * select
	 */
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userMapper.loadUserByUserNmae(username);
		if(null != user) {
			return user;
		}else {
			return new User();
		}
	}
	
	public int updateUserEmail(String email, Long id) {
		return userMapper.updateUserEmail(email, id);
	}
	
	public int updateUserEnabled(Boolean enabled, Long uid) {
		return userMapper.updateUserEnabled(enabled, uid);
	}
	
	public int deleteUserById(long uid) {
		return userMapper.deleteUserById(uid);
	}
	
	public User getUserById(@Param("id") Long id) {
		return userMapper.getUserById(id);
	}
	
	public long reg(User user) {
		return userMapper.reg(user);
	}

}

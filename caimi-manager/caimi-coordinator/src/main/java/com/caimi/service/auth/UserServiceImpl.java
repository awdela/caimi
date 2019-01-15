package com.caimi.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caimi.service.cache.RedisCacheManager;
import com.caimi.service.dao.UserDao;
import com.caimi.service.repository.AbstractEntity;
import com.caimi.service.repository.entity.User;
import com.caimi.service.repository.entity.UserEntity;
import com.caimi.util.StringUtil;
import com.caimi.util.UUIDUtil;
import com.caimi.util.beans.RespBean;

@Service
@Transactional
public class UserServiceImpl implements UserService {// , UserDetailsService

	@Autowired
	private UserDao userDao;

    @Autowired
    private RedisCacheManager<String, AbstractEntity> redis;

    // @Override
    // public UserDetails loadUserByUsername(String username) throws
    // UsernameNotFoundException {
    // UserEntity user = (UserEntity) userDao.getByName(username);
    // if (user == null) {
    // throw new UsernameNotFoundException("用户名不存在");
    // }
    // Role role = userDao.getRole(user.getId());
    // //用于添加用户的权限,只要把用户权限添加到authorities
    // List<SimpleGrantedAuthority> authorities = new
    // ArrayList<SimpleGrantedAuthority>();
    // authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
    // return user;
    // }

	/**
	 * @param user
	 * @return 0 success
	 * @return 1 username not exist
	 * @return 2 failed
	 */
	@Override
    public RespBean login(User user) {
		User userEntity = userDao.getByName(user.getName());
        if (userEntity == null) {
            return new RespBean("error", "change your username!");
		}
        String passWord = StringUtil.md5(user.getPassword());
        if (passWord.equals(userEntity.getPassword())) {
            // creat token
            String token = TOKEN_PREFIX + UUIDUtil.genId();
            redis.hset(REDIS_USER_SESSION_KEY, token, (UserEntity) userEntity);
            redis.expire(token, REDIS_USER_SESSION_EXPIRE);
            return new RespBean("success", "token");
		}
        return new RespBean("error", "need to regist or login!");
	}

	/**
	 * @param user
	 * @return 0 success
	 * @return 1 repeat username
	 * @return 2 failed
	 */
	@Override
    public RespBean regist(User user) {
		User repeatUserName = userDao.getByName(user.getName());
		if(repeatUserName!=null) {
            return new RespBean("error", "change your username!");
		}
		userDao.save(user);
        return new RespBean("success", "welcome to caimi!");
	}

	@Override
    public RespBean deleteUser(User user) {
        User existUser = userDao.getByName(user.getName());
        if (null != existUser) {
            userDao.delete(existUser);
            return new RespBean("success", "logout caimi!");
        }
        return new RespBean("error", "need to regist or login!");
	}

    @Override
    public RespBean updateUser(User user) {
        User updateUser = userDao.update(user);
        if (null != updateUser) {
            return new RespBean("success", "update done");
        }
        return new RespBean("error", "update error");
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

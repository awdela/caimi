package com.caimi.service.auth;

import com.caimi.service.repository.entity.User;
import com.caimi.util.beans.RespBean;

public interface UserService {

    public static final String TOKEN_PREFIX = "tkn_";

    public static final String REDIS_USER_SESSION_KEY = "redis_session";

    public static final long REDIS_USER_SESSION_EXPIRE = 1000;

    public RespBean login(User user);

    public RespBean regist(User user);

    public RespBean deleteUser(User user);

    public RespBean updateUser(User user);

	public User getUserById(String id);

	public User getUserByName(String name);

}

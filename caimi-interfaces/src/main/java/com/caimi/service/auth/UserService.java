package com.caimi.service.auth;

import com.caimi.service.repository.entity.User;

public interface UserService {

	public int login(User user);

	public int regist(User user);

    public int deleteUser(User user);

    public int updateUser(User user);

	public User getUserById(String id);

	public User getUserByName(String name);

}

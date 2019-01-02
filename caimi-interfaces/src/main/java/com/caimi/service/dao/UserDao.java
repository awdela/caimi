package com.caimi.service.dao;

import com.caimi.service.repository.entity.Role;
import com.caimi.service.repository.entity.User;

public interface UserDao {

	User getById(String id);

	User getByName(String userName);

    User save(User user);

    User update(User user);

    User delete(User user);

	Role getRole(String id);

}

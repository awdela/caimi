package com.caimi.service.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caimi.service.repository.BORepository;
import com.caimi.service.repository.entity.Role;
import com.caimi.service.repository.entity.User;
import com.caimi.service.repository.entity.UserEntity;
import com.caimi.util.StringUtil;
import com.caimi.util.UUIDUtil;
import com.caimi.util.concurrent.CloseableLock;

@Service
public class UserDaoImpl implements UserDao{

	private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
	@Autowired
	private BORepository repository;
	@Autowired
	private RoleUserDao roleUserDao;

	@Override
	public User getById(String id) {
        UserEntity entity = repository.get(UserEntity.class, id);
		return entity;
	}

	@Override
	public User getByName(String userName) {
        // search from database
        List<UserEntity> entities = repository.search(UserEntity.class, "name = " + userName);
        if (!entities.isEmpty()) {
            return entities.get(0);
        }
        return null;
	}

	@Override
    public User save(User user) {
        if (StringUtil.isEmpty(user.getId())) {
            user.setId(User.ID_PREFIX_USER + UUIDUtil.genId());
        }
        // Digest the passwd
        if (StringUtil.isEmpty(user.getPassword())) {
            return null;
        }
        user.setPassword(StringUtil.md5(user.getPassword()));
		try(CloseableLock lock = new CloseableLock(repository.getLock(null))){
			repository.beginTransaction(false);
			repository.save(user);
			repository.endTransaction(true);
            return user;
		}catch(Throwable t) {
			try {
				repository.endTransaction(false);
			}catch(Throwable t2) {
				logger.error("Repository save data failed: "+user.toString(), t2);
			}
		}
        return null;
	}

	@Override
    public User delete(User user) {
        try (CloseableLock lock = new CloseableLock(repository.getLock(null))) {
            repository.beginTransaction(false);
            repository.remove(user);
            repository.endTransaction(true);
            return user;
        } catch (Throwable t) {
            try {
                repository.endTransaction(false);
            } catch (Throwable t2) {
                logger.error("Repository save data failed: " + user.toString(), t2);
            }
        }
        return null;
	}

    @Override
    public User update(User user) {
        // 根据用户名来更新
        User user0 = getById(user.getId());
        user.setId(user0.getId());
        return save(user);
    }

	@Override
	public Role getRole(String id) {
		return roleUserDao.getRole(id);
	}

}
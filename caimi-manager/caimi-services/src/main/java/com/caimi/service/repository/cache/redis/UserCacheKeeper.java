package com.caimi.service.repository.cache.redis;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caimi.service.repository.entity.UserEntity;
import com.caimi.util.StringUtil;

public class UserCacheKeeper extends RedisCacheKeeper<UserEntity>{

    private static final Logger logger = LoggerFactory.getLogger(UserCacheKeeper.class);

    private static final String className = UserCacheKeeper.class.getSimpleName();

	public UserCacheKeeper(Class<UserEntity> mainClasses) {
		super(mainClasses);
	}

	@SuppressWarnings("unchecked")
	@Override
    protected void put0(UserEntity bo) {
        String key = bo.getName();
//		// 用户表中缓存用户名称
		redisMgr.hset(className, key, bo);
	}


	@SuppressWarnings("unchecked")
	@Override
	protected UserEntity get0(Object key) {
		return (UserEntity) redisMgr.hget(className, key);
	}

	@SuppressWarnings("unchecked")
	@Override
    protected UserEntity remove0(Object key) {
        UserEntity entity = get0(key);
        redisMgr.hdel(className, key);
        return entity;
	}

    @SuppressWarnings("unchecked")
    @Override
    public int reloadAll() {
        // 删除所有缓存数据并从数据库中加载
        redisMgr.del(className);
        List<UserEntity> entities = container.getAccessor(UserEntity.class).loadAllEntities(UserEntity.class, null);
        logger.info(UserEntity.class.getSimpleName() + " reloadAll and size: " + entities.size());
        putAll(entities);
        return entities.size();
    }

    /**
     * 使用反射实现,不建议使用此方法
     */
    @SuppressWarnings({ "unchecked" })
    @Override
    protected List<UserEntity> search0(String searchExpr) {
        List<String[]> searchExps = StringUtil.splitKVs(searchExpr);
        // get all userentity
        List<UserEntity> result = new LinkedList<>();
        List<UserEntity> entities = redisMgr.hvals(className);
        try {
            for (UserEntity entity : entities) {
                boolean flag = true;
                for (String[] searchExp : searchExps) {
                    Method m = UserEntity.class.getMethod("get" + StringUtil.upperFirstString(searchExp[0]));
                    Object value = m.invoke(entity);
                    if (!StringUtil.equals(searchExp[1], (String) value)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    result.add(entity);
                }
            }
        } catch (Exception e) {
            logger.error(className + " search content " + searchExpr + " has error: ", e.getMessage());
        }
        return result;
    }

}

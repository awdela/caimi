package com.caimi.service.repository.cache;

import com.caimi.service.repository.entity.UserEntity;

public class UserCacheKeeper extends RedisCacheKeeper<UserEntity>{

	private static final String className = UserCacheKeeper.class.getSimpleName();
	
	public UserCacheKeeper(Class<UserEntity> mainClasses) {
		super(mainClasses);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void put0(Object key, UserEntity bo) {
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
	protected void remove0(Object key) {
		redisMgr.del(key);;
	}

}

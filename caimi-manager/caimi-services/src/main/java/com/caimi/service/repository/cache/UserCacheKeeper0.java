package com.caimi.service.repository.cache;

import java.util.List;

import com.caimi.service.repository.entity.UserEntity;

public class UserCacheKeeper0 extends RedisCacheKeeper<UserEntity>{

	 private static final String KEY_REPOSITORY_USER_CACHE = "caimi.repository.user.cache";
	 
	public UserCacheKeeper0(Class<UserEntity> mainClasses) {
		super(mainClasses);
	}
	
	@Override
	public List<UserEntity> search(Class<UserEntity> boClass, String searchExpr) {
		return super.search(boClass, searchExpr);
	}

	@Override
	public List<Object[]> search(Class<UserEntity> boClass, String searchExpr, String[] fields) {
		return super.search(boClass, searchExpr, fields);
	}

	@Override
	public void putAll(List<UserEntity> list) {
		super.putAll(list);
	}

	@Override
	protected void put0(UserEntity bo) {
		// 用户表中缓存用户名称
		redisMgr.hset(KEY_REPOSITORY_USER_CACHE, bo.getName(), bo.toJSON().toString());
	}
	
	@Override
	protected UserEntity get0(String key) {
		String userEntity = redisMgr.hget(KEY_REPOSITORY_USER_CACHE, key);
		return null;
	}
	
	
	// TODO
	@Override
	protected UserEntity get0(int key, Object keyId) {
		return null;
	}

	// TODO
	@Override
	protected Object getId0(int key, Object keyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected UserEntity remove0(UserEntity bo) {
		// TODO Auto-generated method stub
		return null;
	}

}

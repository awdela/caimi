package com.caimi.service.cahce;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;

import com.caimi.service.repository.entity.cache.UserEntity;

public class UserCacheKeeper extends IgniteCacheKeeper<UserEntity>{

	private static final String LOCK_NAME = "caimi.repository.user.lock";
	
	private static final String KEY_REPOSITORY_USER_CACHE_LOADNODE = "caimi.repository.user.cache.loadNode";
	
	private static final String KEY_REPOSITORY_USER_CACHE_BYID = "caimi.repository.user.cacheById";
	
	public UserCacheKeeper() {
		super(UserEntity.class, KEY_REPOSITORY_USER_CACHE_LOADNODE, LOCK_NAME);
	}

	@Override
	protected void initIgniteCache() {
		Ignite ignite = clusterMgr.getIgnite();
		{
			CacheConfiguration<String, UserEntity> cacheCfg = new CacheConfiguration<>();
			cacheCfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
			cacheCfg.setCacheMode(CacheMode.REPLICATED);
			cacheCfg.setName(KEY_REPOSITORY_USER_CACHE_BYID);
			cacheCfg.setIndexedTypes(String.class, UserEntity.class);
			
		}
	}

	@Override
	public void destroy() {
		
	}

	@Override
	public int reloadAll() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected IgniteCache<Object, UserEntity> getMainCache() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected UserEntity get0(int key, Object keyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object getId0(int key, Object keyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void put0(UserEntity t) {
		String UserId = t.getId();
		
	}

	@Override
	protected UserEntity remove0(Object boId) {
		// TODO Auto-generated method stub
		return null;
	}

}

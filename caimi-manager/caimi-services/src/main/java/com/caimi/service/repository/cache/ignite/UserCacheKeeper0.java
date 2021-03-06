 package com.caimi.service.repository.cache.ignite;

import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;

import com.caimi.service.cache.IgniteBusinessEntityCacheKeeper;
import com.caimi.service.repository.entity.UserEntity;

public class UserCacheKeeper0 extends IgniteBusinessEntityCacheKeeper<UserEntity>{

	private static final String LOCK_NAME = "caimi.repository.user.lock";

	private static final String KEY_REPOSITORY_USER_CACHE_LOADNODE = "caimi.repository.user.cache.loadNode";

	private static final String KEY_REPOSITORY_USER_CACHE_BYID = "caimi.repository.user.cacheById";

	public UserCacheKeeper0() {
		super(UserEntity.class, KEY_REPOSITORY_USER_CACHE_LOADNODE, LOCK_NAME);
	}

	/**
	 * User Id -> Object
	 */
	private IgniteCache<String, UserEntity> users;

	@Override
	protected void initIgniteCache() {
		Ignite ignite = clusterMgr.getIgnite();
		{
			CacheConfiguration<String, UserEntity> cacheCfg = new CacheConfiguration<>();
			cacheCfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
			cacheCfg.setCacheMode(CacheMode.REPLICATED);
			cacheCfg.setName(KEY_REPOSITORY_USER_CACHE_BYID);
			cacheCfg.setIndexedTypes(String.class, UserEntity.class);
			users = ignite.getOrCreateCache(cacheCfg);
		}
	}

	@Override
	public int reloadAll() {
		initIgniteCache();
		users.clear();
		/*
		 * 同步数据库
		 * container在initIgnite的时候就初始化了
		 */
		List<UserEntity> list = container.getAccessor(UserEntity.class).loadAllEntities(UserEntity.class, null);
//		putAll(list);
		updateCacheLoadNodeName();
		return list.size();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected IgniteCache<Object, UserEntity> getMainCache() {
		return (IgniteCache)users;
	}

	@Override
	protected void put0(UserEntity t) {
		String UserId = t.getId();
		users.put(UserId, t);
	}

	@Override
	protected UserEntity remove0(Object boId) {
		String userId = boId.toString();
		UserEntity entity = users.getAndRemove(userId);
		return entity;
	}

	@Override
	public int reload(List<UserEntity> list) {
		// TODO Auto-generated method stub
		return 0;
	}

    @Override
    public void put(UserEntity t) {
        // TODO Auto-generated method stub

    }

    @Override
    public void putAll(List<UserEntity> list) {
        // TODO Auto-generated method stub

    }

}

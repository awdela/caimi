package com.caimi.service.repository.cache;

import com.caimi.service.repository.entity.AbstractBusinessEntity;

public abstract class RedisBusinessEntityCacheKeeper<T extends AbstractBusinessEntity> extends RedisCacheKeeper<T> {

	public RedisBusinessEntityCacheKeeper(Class<T> mainClasses) {
		super(mainClasses);
	}

	@Override
	protected T get0(int key, Object keyId) {
		switch(key) {
		case KEY_ID:
		{
//			T result = getM
		}
		}
		return null;
	}

	@Override
	public Object getId(int key, Object keyId) {
		// TODO Auto-generated method stub
		return super.getId(key, keyId);
	}
	
	

}

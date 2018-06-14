package com.caimi.service.cahce;

import java.util.List;
import java.util.concurrent.locks.Lock;

import com.caimi.service.repository.AbstractEntity;
import com.caimi.service.repository.BOCacheContainer;
import com.caimi.service.repository.BOCacheKeeper;

public abstract class RedisCacheKeeper<T extends AbstractEntity> implements BOCacheKeeper<T>{

	@Override
	public void init(BOCacheContainer container) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public T get(int key, Object keyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getId(int key, Object keyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> search(Class<T> boClass, String searchExpr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object[]> search(Class<T> boClass, String searchExpr, String[] fields) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void put(T t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void putAll(List<T> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public T remove(Object boId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int reloadAll() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int reload(List<T> list) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Lock getLock() {
		// TODO Auto-generated method stub
		return null;
	}

	
}

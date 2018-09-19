package com.caimi.service.cache;

import java.util.List;
import java.util.concurrent.locks.Lock;

import com.caimi.service.repository.AbstractEntity;
import com.caimi.service.repository.BOCacheContainer;
import com.caimi.service.repository.BOCacheKeeper;

public abstract class RedisCacheKeeper<T extends AbstractEntity> implements BOCacheKeeper<T>{

	@Override
	public void init(BOCacheContainer container) {

	}

	@Override
	public void destroy() {

	}

	@Override
    public T get(Object keyId) {
		return null;
	}

	@Override
	public Object getId(int key, Object keyId) {
		return null;
	}

	@Override
	public List<T> search(Class<T> boClass, String searchExpr) {
		return null;
	}

	@Override
	public List<Object[]> search(Class<T> boClass, String searchExpr, String[] fields) {
		return null;
	}

	@Override
	public void put(T t) {

	}

	@Override
	public void putAll(List<T> list) {

	}

	@Override
	public T remove(Object boId) {
		return null;
	}

	@Override
	public int reloadAll() {
		return 0;
	}

	@Override
	public int reload(List<T> list) {
		return 0;
	}

	@Override
	public Lock getLock() {
		return null;
	}


}

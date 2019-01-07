package com.caimi.service.repository.cache.redis;

import java.util.List;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caimi.service.cache.RedisCacheManager;
import com.caimi.service.repository.AbstractEntity;
import com.caimi.service.repository.BOCacheContainer;
import com.caimi.service.repository.BOCacheKeeper;
import com.caimi.service.repository.BORepository;

public abstract class RedisCacheKeeper<T extends AbstractEntity> implements BOCacheKeeper<T>{

	private static final Logger logger = LoggerFactory.getLogger(RedisCacheKeeper.class);

    protected Class<T> mainCacheClass;

    protected BOCacheContainer container;

    protected BORepository repository;

//    protected RedisClusterMgr clusterMgr;

    protected RedisCacheManager redisMgr;

    // 缓存单个键
    protected abstract void put0(T bo);

    /* 可以缓存多个key
     * TODO
     */
    protected abstract T get0(Object key);

    protected abstract T remove0(Object key);

    protected abstract List<T> search0(String searchExpr);

    public RedisCacheKeeper(Class<T> mainClasses) {
        this.mainCacheClass = mainClasses;
    }

	@Override
	public void init(BOCacheContainer container) {
        this.container = container;
        this.repository = container.getRepository();
        this.redisMgr = container.getBean(RedisCacheManager.class);
	}

    @Override
    public void put(T t) {
        if (t != null) {
            put0(t);
        }
    }

    @Override
    public void putAll(List<T> entityLists) {
        for (T entity : entityLists) {
            put(entity);
        }
    }

	@Override
    public T get(Object keyId) {
		return get0(keyId);
	}

    // TODO
	@Override
	public Object getId(int key, Object keyId) {
		return null;
	}

    /**
     * "key=value,key0=value0"
     */
	@Override
    public List<T> search(Class boClass, String searchExpr) {
        return search0(searchExpr);
	}

    // not support
	@Override
	public List<Object[]> search(Class<T> boClass, String searchExpr, String[] fields) {
		return null;
	}

	@Override
    public T remove(Object boId) {
        return remove0(boId);
	}

	@Override
	public int reload(List<T> list) {
        putAll(list);
        return list.size();
	}

	@Override
	public Lock getLock() {
		return null;
	}


}

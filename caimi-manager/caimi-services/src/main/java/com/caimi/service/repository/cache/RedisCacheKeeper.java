package com.caimi.service.repository.cache;

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
    protected abstract void put0(Object key, T bo);
    
    /* 可以缓存多个key
     * TODO
     */
    protected abstract T get0(Object key);
    
    protected abstract void remove0(Object key);

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
	public void destroy() {

	}

	@Override
    public T get(Object keyId) {
		return get0(keyId);
	}

	@Override
	public Object getId(int key, Object keyId) {
		return null;
	}

	/**
	 * redis不支持
	 * ignite支持
	 */
	@Override
	public List<T> search(Class<T> boClass, String searchExpr) {
		return null;
	}

	@Override
	public List<Object[]> search(Class<T> boClass, String searchExpr, String[] fields) {
		return null;
	}

	@Override
	public void put(Object key, T t) {
		if (t==null) {
			return;
		}
		put0(key, t);
	}

	@Override
	public void putAll(Object key, List<T> list) {
		for(T t:list) {
			put(key, t);
		}
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

package com.caimi.service.cache;

import java.util.List;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caimi.service.cluster.RedisClusterMgr;
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
    
    protected RedisSingleMgr redisMgr;

    protected abstract T get0();

    public RedisCacheKeeper(Class<T> mainClasses) {
        this.mainCacheClass = mainClasses;
    }

	@Override
	public void init(BOCacheContainer container) {
        this.container = container;
        this.repository = container.getRepository();
        this.redisMgr = container.getBean(RedisSingleMgr.class);
	}

	@Override
	public void destroy() {

	}

	@Override
    public T get(Object keyId) {
        String key = mainCacheClass.getSimpleName() + "_" + String.valueOf(keyId);
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

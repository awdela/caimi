package com.caimi.service.cahce;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import javax.cache.Cache;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteLock;
import org.apache.ignite.cache.CachePeekMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caimi.service.BORepository;
import com.caimi.service.cluster.IgniteClusterMgr;
import com.caimi.service.repository.AbstractEntity;
import com.caimi.service.repository.BOCacheContainer;
import com.caimi.service.repository.BOCacheKeeper;
import com.caimi.util.StringUtil;
import com.caimi.util.SystemUtil;

public abstract class IgniteCacheKeeper<T extends AbstractEntity> implements BOCacheKeeper<T>{
	
	private static final Logger logger = LoggerFactory.getLogger(IgniteCacheKeeper.class);
	
	protected static final String INIT_LOCK_NAME = "caimi.repository.cache.initLock";
	
	/**
	 * 当前活动的Ignite线程,key:thread id,value:thread enter time
	 */
	private static ConcurrentHashMap<Long,Long> igniteThreads = new ConcurrentHashMap<>();
	
	protected BOCacheContainer container;
	
	protected BORepository repository;
	
	protected Class<T> mainCacheClass;
	
	protected IgniteClusterMgr clusterMgr;
	
	protected String keyCacheLoadNode;
	
	private String igniteLockName = null;
	
	private volatile IgniteLock igniteLock = null;
	
	protected abstract void initIgniteCache();
	
	protected abstract IgniteCache<Object, T> getMainCache();
	
	protected abstract T get0(int key, Object keyId);
	
	protected abstract Object getId0(int key, Object keyId);
	
	public IgniteCacheKeeper(Class<T> mainClasses, String keyCacheLoadNode, String igniteLockName) {
		this.mainCacheClass = mainClasses;
		this.keyCacheLoadNode = keyCacheLoadNode;
		this.igniteLockName = igniteLockName;
	}

	/**
	 * 分布式初始化Cache
	 */
	@Override
	public void init(BOCacheContainer container) {
		this.container = container;
		this.repository = container.getRepository();
		clusterMgr = container.getBean(IgniteClusterMgr.class);
		initAndLoadData();
		return; 
	}

	private void initAndLoadData() {
		Ignite ignite = clusterMgr.getIgnite();
		igniteLock = clusterMgr.getIgnite().reentrantLock(igniteLockName, true, false, true);
		IgniteLock lock = ignite.reentrantLock(igniteLockName, true, false, true);
		lock.lock();
		try {
			initIgniteCache();
			String loadNode = clusterMgr.dget(keyCacheLoadNode);
			if ( SystemUtil.isApplicationMasterMode() || StringUtil.isEmpty(loadNode) || getMainCache().size(CachePeekMode.PRIMARY)==0 ) {
				reloadAll();
			}else {
				logger.info("Repository entity cache "+keyCacheLoadNode+" was loaded by "+loadNode);
			}
		}finally {
			lock.unlock();
		}
		
	}
	
	private void cacheStateCheck() {
		Cache<Object, T> mainCache = getMainCache();
		boolean needInitCache = true;
		try {
			needInitCache = mainCache==null || mainCache.isClosed();
		}catch(Throwable t) {
			needInitCache = true;
			logger.error("Check cache state failed", t);
		}
		if (needInitCache) {
			initIgniteCache();
		}
	}

	@Override
	public T get(int key, Object keyId) {
		
		cacheStateCheck();
		
		long tid = Thread.currentThread().getId();
		igniteThreads.put(tid, System.currentTimeMillis());
		try{
			return get0(key, keyId);
		}catch(java.lang.IllegalStateException ise) {
			initAndLoadData();
			return get0(key, keyId);
		}finally {
			igniteThreads.remove(tid);
		}
	}

	@Override
	public Object getId(int key, Object keyId) {
		
		cacheStateCheck();

        long tid = Thread.currentThread().getId();
        igniteThreads.put(tid, System.currentTimeMillis());
        try {
            return getId0(key, keyId);
        }catch(java.lang.IllegalStateException ise) {
            initAndLoadData();
            return getId0(key, keyId);
        }finally {
            igniteThreads.remove(tid);
        }
	}

	@Override
	public List<T> search(Class<T> boClass, String searchExpr) {
		
		cacheStateCheck();
		
        long tid = Thread.currentThread().getId();
        igniteThreads.put(tid, System.currentTimeMillis());
        try {
            return search0(boClass, searchExpr);
        }finally {
            igniteThreads.remove(tid);
        }
	}

	private List<T> search0(Class<T> boClass, String searchExpr) {
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
	public int reload(List<T> list) {
		return 0;
	}

	@Override
	public Lock getLock() {
		if (igniteLock!=null) {
			synchronized(this) {
				if ( igniteLock==null ) {
                    igniteLock = clusterMgr.getIgnite().reentrantLock(igniteLockName, true, false, true);
                }
			}
		}else {
			try {
				igniteLock.getHoldCount();
			}catch(Throwable t) {
				igniteLock = null;
                igniteLock = clusterMgr.getIgnite().reentrantLock(igniteLockName, true, false, true);
			}
		}
		return igniteLock;
	}
	
	
	
}

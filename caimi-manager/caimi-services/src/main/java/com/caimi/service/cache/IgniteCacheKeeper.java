package com.caimi.service.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import javax.cache.Cache;
import javax.cache.Cache.Entry;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.IgniteLock;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.SqlQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caimi.service.cluster.IgniteClusterMgr;
import com.caimi.service.repository.AbstractEntity;
import com.caimi.service.repository.BOCacheContainer;
import com.caimi.service.repository.BOCacheKeeper;
import com.caimi.service.repository.BORepository;
import com.caimi.util.StringUtil;
import com.caimi.util.SystemUtil;

public abstract class IgniteCacheKeeper<T extends AbstractEntity> implements BOCacheKeeper<T> {

	private static final Logger logger = LoggerFactory.getLogger(IgniteCacheKeeper.class);

	protected static final String INIT_LOCK_NAME = "caimi.repository.cache.initLock";

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

	protected abstract void put0(T t);

	protected abstract T remove0(Object boId);

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
    public T get(Object keyId) {
		cacheStateCheck();
		try{
            return get0(0, keyId);
		}catch(java.lang.IllegalStateException ise) {
			initAndLoadData();
            return get0(0, keyId);
		}
	}

	@Override
	public Object getId(int key, Object keyId) {
		cacheStateCheck();
        try {
            return getId0(key, keyId);
        }catch(java.lang.IllegalStateException ise) {
            initAndLoadData();
            return getId0(key, keyId);
        }
	}

	@SuppressWarnings("rawtypes")
	public List<T> search(Class boClass, String searchExpr) {
		cacheStateCheck();
		if (StringUtil.isEmpty(searchExpr)) {
			searchExpr = "1=1";
		}
		SqlQuery<Object, T> sqlQuery = new SqlQuery<>(mainCacheClass, searchExpr);
		List<T> result = new ArrayList<>();
		try(QueryCursor<Entry<Object, T>> cursor = getMainCache().query(sqlQuery)){
			for (Entry<Object, T> entry:cursor) {
				result.add(entry.getValue());
			}
		}
		return result;
	}

	@Override
	public List<Object[]> search(Class<T> boClass, String searchExpr, String[] fields) {
		cacheStateCheck();
		if (StringUtil.isEmpty(searchExpr)) {
			searchExpr = "1=1";
		}
		StringBuilder query = new StringBuilder(128);
		query.append("SELECT ");
		boolean appendComma = false;
        for(String field: fields) {
        	if (appendComma) {
                query.append(", ");
            }
            query.append(field).append(" ");
            appendComma = true;
        }
		query.append(mainCacheClass.getSimpleName()).append(" WHERE ").append(searchExpr);
		List<Object[]> result = new ArrayList<>();
		try(FieldsQueryCursor<List<?>> cursor = getMainCache().query(new SqlFieldsQuery(query.toString()))){
			for (Object entry:cursor) {
				result.add((Object[])entry);
			}
		}
		return result;
	}

//	@Override
//	public void put(T t) {
//		if (t == null) {
//			return;
//		}
//		cacheStateCheck();
//		put0(t);
//	}
//
//	@Override
//	public void putAll(List<T> list) {
//		cacheStateCheck();
//		for(T t:list) {
//			put(t);
//		}
//	}

	@Override
	public T remove(Object boId) {
		cacheStateCheck();
		return remove0(boId);
	}

//	@Override
//	public int reload(List<T> list) {
//		putAll(list);
//		return list.size();
//	}

	/**
	 * 同步Ignite集群数据
	 */
	protected void updateCacheLoadNodeName() {
		Ignite ignite = clusterMgr.getIgnite();
		IgniteCluster cluster = ignite.cluster();

		List<String> hostNames = new ArrayList<String>(cluster.localNode().hostNames());
		String cacheLoadNodeName = hostNames.get(0);
		for(String hostName: hostNames) {
			if (hostName.equals("localhost") || hostName.equals("127.0.0.1")) {
				continue;
			}
			cacheLoadNodeName = hostName;
			break;
		}
		clusterMgr.dput(keyCacheLoadNode, cacheLoadNodeName);
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

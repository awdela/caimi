package caimi.service.cache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import org.apache.ignite.IgniteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caimi.service.BORepository;
import com.caimi.service.cluster.IgniteClusterMgr;
import com.caimi.service.repository.BOCacheContainer;
import com.caimi.service.repository.BOCacheKeeper;

import caimi.service.repository.AbstractEntity;

public abstract class IgniteCacheKeeper<T extends AbstractEntity> implements BOCacheKeeper<T>{
	
	private static final Logger logger = LoggerFactory.getLogger(IgniteCacheKeeper.class);
	
	protected static final String INIT_LOCK_NAME = "caimi.repository.cache.initLock";
	
	private static volatile boolean statsRegistered = false;
	
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
	
	public IgniteCacheKeeper(Class<T> mainClasses, String keyCacheLoadNode, String igniteLockName) {
		this.mainCacheClass = mainClasses;
		this.keyCacheLoadNode = keyCacheLoadNode;
		this.igniteLockName = igniteLockName;
	}

	@Override
	public void init(BOCacheContainer container) {
		this.container = container;
		this.repository = container.getRepository();
		clusterMgr = container.getBean(IgniteClusterMgr.class);
		initAndLocalData();
		return;
	}

	private void initAndLocalData() {
		
	}

	@Override
	public void destroy() {
		
	}

	@Override
	public T get(int key, Object keyId) {
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

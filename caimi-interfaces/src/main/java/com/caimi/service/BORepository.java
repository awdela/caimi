package com.caimi.service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;

import com.caimi.service.repository.BOCacheKeeper;
import com.caimi.service.repository.BOEntityAccessor;

/**
 * Business Object Repository
 * <BR> hibernate和ignite共享 
 */
@SuppressWarnings("rawtypes") 
public interface BORepository {
	
	public static String ID_STAR = "*";
	
	/**
	 * for hibernate
	 */
	public BOEntityAccessor getAccessor(Class entityClass);

	public<T> BOCacheKeeper<T> getCacheKeeper(Class<T> boClass);
	
	public Collection<Class> getBOClass();
	
	/**
     * 根据ID前缀返回Entity class, 需要在配置文件中指定idPrefix行
     */
    public Class detectBOClassById(String idOrPrefix);
    
    /**
     * 返回分布式Lock
     */
    public Lock getLock(Class boClass);
    
    public<T> Object getIdBy(Class<T> boClass, int key, Object keyId);
    
    public void save(Object entity);
    
    public void saveAll(List<Object> entities);
    
    public void remove(Object entity);
    
    public<T> List<T> search(Class<T> boClass, String searchExpr);
    
    /**
     * 返回查询指定字段(待实现)
     */
    public<T> List<Object[]> search(Class<T> boClass, String searchExpr, String[] fields);
    
    public void beginTransaction(boolean readOnly);
    
    public void endTransaction(boolean commit);
    
    /**
     * 异步更新 Repository.
     * <BR>该方法保证数据库事务更新. 不需要调用beginTransaction/endTransaction
     */
    public void asyncUpdate(Runnable asyncTask);
    
    /**
     * 重新加载数据，并发送Change Event，通知对应的Listener
     */
    public<T> List<T> reloadAll(Class<T> boClass, List<Object> boIds);
}

package com.caimi.service.repository;

import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * BO实例的缓存维护对象
 */
public interface BOCacheKeeper<T> {

	public static final int KEY_ID = BOEntity.KEY_ID;
	
	public void init(BOCacheContainer container);
	
	public void destroy();
	
	/**
	 * 根据KEY返回缓存的ID
	 * 
	 * @param key 0 代表id
	 * @param keyId
	 * @return cached id or null
	 */
	public T get(int key, Object keyId);
	
	/**
	 * 根据KEY返回缓存的ID
	 * 
	 * @param key 0 代表id
	 * @param keyId
	 * @return cached id or null
	 */
	public Object getId(int key, Object keyId);
	
	/**
	 * CRUD
	 */
	
	public List<T> search(Class<T> boClass, String searchExpr);
	
	public List<Object[]> search(Class<T> boClass, String searchExpr, String[] fields);
	
	public void put(T t);
	
	public void putAll(List<T> list);
	
	public T remove(Object boId);
	
	/**
	 * 清除并重新加载所有数据
	 */
	public int reloadAll();
	
	/**
	 * 重新加载指定数据
	 */
	public int reload(List<T> list);
	
	public Lock getLock();
	
}
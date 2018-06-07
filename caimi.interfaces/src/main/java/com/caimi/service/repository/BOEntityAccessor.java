package com.caimi.service.repository;

import java.util.Collection;
import java.util.List;

/**
 * 业务对象数据访问接口
 */
@SuppressWarnings("rawtypes") 
public interface BOEntityAccessor {
	
	public void init(BOCacheContainer container);
	
	public<T> T loadEntity(Class<T> entityClass, Object boId);
	
	public<T> T loadAllEntities(Class<T> entityClass, List boIds);
	
	public<T> T saveEntity(T t);
	
	public<T> List<T> saveAllEntites(List<T> t);
	
	public<T> List<T> searchEntity(Class<T> entityClass, String searcgExpr);
	
	public<T> List<Object[]> searchEntity(Class<T> entityClass, String searchExpr, String[] fields);
	
	public boolean removeEntity(Object o);
	
	public int removeAllEntites(Collection entityInstances);

}

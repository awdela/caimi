package com.caimi.service;

import java.util.Collection;

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
	
}

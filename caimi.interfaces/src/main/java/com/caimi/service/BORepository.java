package com.caimi.service;

import com.caimi.service.repository.BOEntityAccessor;

/**
 * Business Object Repository
 *  
 */
@SuppressWarnings("rawtypes") 
public interface BORepository {
	
	public static String ID_STAR = "*";
	
	public BOEntityAccessor getAccessor(Class entityClass);

}

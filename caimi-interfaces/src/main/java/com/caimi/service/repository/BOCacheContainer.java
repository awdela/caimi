package com.caimi.service.repository;

import com.caimi.service.beans.BeansContainer;

@SuppressWarnings("rawtypes")
public interface BOCacheContainer extends BeansContainer{

	public BORepository getRepository();

    public BOEntityAccessor getAccessor(Class entityClass);

	public<T> BOCacheKeeper<T> getCacheKeeper(Class<T> boClass);

}

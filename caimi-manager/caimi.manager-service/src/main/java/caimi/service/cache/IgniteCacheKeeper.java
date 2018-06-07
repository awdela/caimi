package caimi.service.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caimi.service.repository.BOCacheKeeper;

import caimi.service.repository.AbstractEntity;

public abstract class IgniteCacheKeeper<T extends AbstractEntity> implements BOCacheKeeper<T>{
	
	private static final Logger logger = LoggerFactory.getLogger(IgniteCacheKeeper.class);
	
	
}

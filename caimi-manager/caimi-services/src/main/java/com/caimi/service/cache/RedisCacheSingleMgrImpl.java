package com.caimi.service.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.caimi.service.repository.AbstractEntity;

@Service
public class RedisCacheSingleMgrImpl implements RedisCacheManager<String, AbstractEntity> {

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheSingleMgrImpl.class);

    @Autowired
    private RedisTemplate<String, AbstractEntity> redisTemplate;

    @PostConstruct
    public void init() {
    }

    @Override
	public boolean expire(String key, long time) {
    	try {
    		if (time>0) {
    			redisTemplate.expire(key, time, TimeUnit.SECONDS);
    		}
    		return true;
    	}catch(Exception e) {
    		logger.error("redis expire time error",e.getMessage());
    	}
    	return false;
	}


	@Override
	public boolean hset(String hkey, String key, AbstractEntity entity) {
		return hset(hkey, key, entity, -1);
	}

	public boolean hset(String hkey, String key, AbstractEntity entity, long time) {
		try {
			redisTemplate.opsForHash().put(hkey, key, entity);
			if (time>0) {
				expire(key, time);
			}
			return true;
		}catch(Exception e) {
			logger.error("redis hset data error: "+entity.toString(),e.getMessage());
		}
		return false;
	}

	@Override
	public AbstractEntity hget(String hkey, String key) {
		return (AbstractEntity) redisTemplate.opsForHash().get(hkey, key);
	}

    @Override
    public List<Object> hvals(String hkey) {
        return redisTemplate.opsForHash().values(hkey);
    }

	@Override
	public void hdel(String hkey, String key) {
		redisTemplate.opsForHash().delete(hkey, key);
	}

	@Override
	public boolean hHasKey(String hkey, String key) {
		return redisTemplate.opsForHash().hasKey(hkey, key);
	}

	@Override
	public void del(String key) {
		redisTemplate.opsForHash().delete(key);
	}

	@Override
	public long decr(String key, long delta) {
		return 0;
	}

    @Override
    public boolean getStatus() {
        boolean live = true;
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
        RedisConnection conn = null;
        try {
            conn = RedisConnectionUtils.getConnection(factory);
            live = conn == null || conn.isClosed();
        } catch (Exception e) {
            live = false;
            logger.error("Check cache state failed", e);
        }
        return live;
    }

	@Override
	public long incr(String key) {
		return 0;
	}

	@Override
	public boolean set(String key, AbstractEntity entity) {
		return false;
	}

	@Override
	public boolean set(String key, AbstractEntity entity, long time) {
		return false;
	}

	@Override
	public AbstractEntity get(String key) {
		return null;
	}

}

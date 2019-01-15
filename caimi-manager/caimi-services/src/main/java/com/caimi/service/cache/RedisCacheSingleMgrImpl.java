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
    public boolean expire(String keyId, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(keyId, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            logger.error("redis expire time error", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean hset(String hkey, String keyId, AbstractEntity entity) {
        return hset(hkey, keyId, entity, -1);
    }

    public boolean hset(String hkey, String keyId, AbstractEntity entity, long time) {
        try {
            redisTemplate.opsForHash().put(hkey, keyId, entity);
            if (time > 0) {
                expire(keyId, time);
            }
            return true;
        } catch (Exception e) {
            logger.error("redis hset data error: " + entity.toString(), e.getMessage());
        }
        return false;
    }

    @Override
    public AbstractEntity hget(String hkey, String keyId) {
        return (AbstractEntity) redisTemplate.opsForHash().get(hkey, keyId);
    }

    @Override
    public List<Object> hvals(String hkey) {
        return redisTemplate.opsForHash().values(hkey);
    }

    @Override
    public void hdel(String hkey, String keyId) {
        try {
            redisTemplate.opsForHash().delete(hkey, keyId);
        } catch (Exception e) {
            logger.error("remove " + hkey + " id: " + keyId + "failed", e);
        }
    }

    @Override
    public boolean hHasKey(String hkey, String keyId) {
        return redisTemplate.opsForHash().hasKey(hkey, keyId);
    }

    @Override
    public void del(String keyId) {
        try {
            redisTemplate.opsForHash().delete(keyId);
        } catch (Exception e) {
            logger.error("remove " + " id: " + keyId + "failed", e);
        }
    }

    @Override
    public long decr(String keyId, long delta) {
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
    public long incr(String keyId) {
        return 0;
    }

    @Override
    public boolean set(String keyId, AbstractEntity entity) {
        return false;
    }

    @Override
    public boolean set(String keyId, AbstractEntity entity, long time) {
        return false;
    }

    @Override
    public AbstractEntity get(String keyId) {
        return null;
    }

}

package com.caimi.service.repository.cache;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caimi.service.cache.RedisSingleMgr;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisSingleMgrImpl implements RedisSingleMgr {

    private static final Logger logger = LoggerFactory.getLogger(RedisSingleMgrImpl.class);

    @Autowired
    private JedisPool jedisPool;

    @PostConstruct
    public void init() {
    }

    @Override
    public String get(String key) {
        Jedis jedis = jedisPool.getResource();
        String result = "";
        try {
            result = jedis.get(key);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return result;
    }

    @Override
    public String set(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        String result = "";
        try {
            result = jedis.set(key, value);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return result;
    }

    @Override
    public String hget(String hkey, String key) {
        Jedis jedis = jedisPool.getResource();
        String result = "";
        try {
            result = jedis.hget(hkey, key);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return result;
    }

    @Override
    public long hset(String hkey, String key, String value) {
        Jedis jedis = jedisPool.getResource();
        Long result;
        try {
            result = jedis.hset(hkey, key, value);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return result;
    }

    @Override
    public long incr(String key) {
        Jedis jedis = jedisPool.getResource();
        Long result;
        try {
            result = jedis.incr(key);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return result;
    }

    @Override
    public long expire(String key, int second) {
        Jedis jedis = jedisPool.getResource();
        Long result;
        try {
            result = jedis.expire(key, second);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return result;
    }

    @Override
    public long del(String key) {
        Jedis jedis = jedisPool.getResource();
        Long result;
        try {
            result = jedis.del(key);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return result;
    }

    @Override
    public long hdel(String hkey, String key) {
        Jedis jedis = jedisPool.getResource();
        Long result;
        try {
            result = jedis.hdel(hkey, key);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return result;
    }

}

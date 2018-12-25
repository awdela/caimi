package com.caimi.service.cluster;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.caimi.service.CaimiContants;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisClusterMgrImpl implements RedisClusterMgr, CaimiContants {

    private JedisCluster cluster;
    private volatile boolean stop;

    // @PostConstruct
    public void init() {
        if (stop) {
            return;
        }
        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        for (String redisNode : REDIS_CLUSTER.split(",")) {
            String[] hp = redisNode.split(":");
            jedisClusterNodes.add(new HostAndPort(hp[0], Integer.parseInt(hp[1])));
        }
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(500);
        config.setMaxIdle(1000 * 60);
        config.setMaxWaitMillis(1000 * 10);
        config.setTestOnBorrow(true);
        cluster = new JedisCluster(jedisClusterNodes, config);
    }

    @Override
    public Object getRedis() {
        checkRedisState();
        return cluster;
    }

    @Override
    public String dget(String key) {
        checkRedisState();
        return cluster.get(key);
    }

    @Override
    public String dput(String key, String value) {
        checkRedisState();
        return cluster.set(key, value);
    }

    public void destroy() {
        stop = true;
        if (cluster != null) {
            cluster.close();
            cluster = null;
        }
    }

    private synchronized void checkRedisState() {
        if (stop || cluster != null) {
            return;
        }
        init();
    }

}

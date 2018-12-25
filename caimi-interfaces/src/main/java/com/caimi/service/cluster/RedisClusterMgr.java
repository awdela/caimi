package com.caimi.service.cluster;

/**
 * Redis集群版
 */
public interface RedisClusterMgr {

	public Object getRedis();

	public String dget(String key);

	public String dput(String key, String value);

}

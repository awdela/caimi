package com.caimi.service.cluster;

public interface RedisClusterMgr {
	
	public Object getRedis();
	
	public String dget(String key);

	public String dput(String key, String value);
	
}

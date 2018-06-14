package com.caimi.service.cluster;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class RedisClusterMgrImpl implements RedisClusterMgr{

	@PostConstruct
	public void init() {
		
	}
	
	@Override
	public Object getRedis() {
		return null;
	}

	@Override
	public String dget(String key) {
		return null;
	}

	@Override
	public String dput(String key, String value) {
		return null;
	}

}

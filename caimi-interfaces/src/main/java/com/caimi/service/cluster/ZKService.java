package com.caimi.service.cluster;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;

public interface ZKService {
	
	public boolean isConnected();
	
	public void addListener(ZKListener listener);

	public boolean exists(String path) throws Exception;
	
	public String get(String path) throws Exception;
	
	/**
	 * @param watcher 是CuratorWatcher实例
	 */
	public List<String> getChildren(String path, Object watcher) throws Exception;
	
	/**
	 * 将数据保存到节点下
	 * 
	 * @return 实际的node path路径
	 */
	public String put(CreateMode mode, String path, String data) throws Exception;
	
	/**
	 * 分布式锁
	 */
	public boolean tryLock(String lockPath, long timeout, TimeUnit unit) throws InterruptedException;
	
	public boolean unLock();
	
}

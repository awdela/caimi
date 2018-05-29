package com.caimi.service.cluster;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

public interface ZKService {
	
	public void addListener(ZKListener listener);

	public boolean exists(String path) throws Exception;
	
	public String get(String path) throws Exception;
	
	public List<String> getChildren(String path) throws Exception;
	
	public List<String> getChildren(String path, Watcher watcher) throws Exception;
	
	/**
	 * 将数据保存到节点下
	 * 
	 * @return 实际的node path路径
	 */
	public String put(CreateMode mode, String path, String data) throws Exception;
	
}

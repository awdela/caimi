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
	 * �����ݱ��浽�ڵ���
	 * 
	 * @return ʵ�ʵ�node path·��
	 */
	public String put(CreateMode mode, String path, String data) throws Exception;
	
}

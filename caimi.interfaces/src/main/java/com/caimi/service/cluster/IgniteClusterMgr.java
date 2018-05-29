package com.caimi.service.cluster;

import org.apache.ignite.Ignite;

/**
 * Ignite 集群管理节点
 * <BR>分主节点(masterNode)和工作节点(DataNode)
 * <BR>主节点启动时将MaintenanceMode设置为true,启动完毕后设置false
 */
public interface IgniteClusterMgr {
	
	public Ignite getIgnite();
	
	/**
	 * 从一个分布式Map中获取数据
	 * 
	 * @param key
	 * @return
	 */
	public String dget(String key);

	/**
	 * 保存数据到分布式Map
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public String dput(String key, String value);
	
}

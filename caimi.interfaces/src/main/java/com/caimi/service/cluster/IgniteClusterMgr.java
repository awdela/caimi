package com.caimi.service.cluster;

import org.apache.ignite.Ignite;

/**
 * Ignite ��Ⱥ����ڵ�
 * <BR>�����ڵ�(masterNode)�͹����ڵ�(DataNode)
 * <BR>���ڵ�����ʱ��MaintenanceMode����Ϊtrue,������Ϻ�����false
 */
public interface IgniteClusterMgr {
	
	public Ignite getIgnite();
	
	/**
	 * ��һ���ֲ�ʽMap�л�ȡ����
	 * 
	 * @param key
	 * @return
	 */
	public String dget(String key);

	/**
	 * �������ݵ��ֲ�ʽMap
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public String dput(String key, String value);
	
}

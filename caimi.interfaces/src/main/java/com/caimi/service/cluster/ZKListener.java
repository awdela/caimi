package com.caimi.service.cluster;

/**
 * zookeeper���ӶϿ�/����
 */
public interface ZKListener {

	public void onReconnected(ZKService zkService);
	
}

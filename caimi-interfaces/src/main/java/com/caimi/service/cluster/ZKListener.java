package com.caimi.service.cluster;

/**
 * zookeeper连接断开/重连
 */
public interface ZKListener {

	public void onReconnected(ZKService zkService);
	
}

package com.caimi.service.cluster;

/**
 * zookeeper连接断开/重连
 */
public interface ZKListener {

	public void onStateChanged(ZKService zkService, ZKConnectionState newState);
	
}

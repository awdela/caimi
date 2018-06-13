package com.caimi.service.cluster;

public enum ZKConnectionState {

	CONNECTED(true),
	SUSPENDED(false),
	RECONNECTED(true),
	LOST(false),
	READ_ONLY(true);
	
	private boolean connected;
	
	ZKConnectionState(boolean v) {
		this.connected = v;
	}
	
	public boolean isConnected() {
		return connected;
	}
}

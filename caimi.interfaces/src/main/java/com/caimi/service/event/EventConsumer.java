package com.caimi.service.event;

/**
 * �¼����ѽӿڣ�kafka��es
 */
public interface EventConsumer {

	public void consume();
	
	public void consume(String str);
	
}

package com.caimi.service.event;

/**
 * 事件消费接口：kafka、es
 */
public interface EventConsumer {

	public void consume();
	
	public void consume(String str);
	
}

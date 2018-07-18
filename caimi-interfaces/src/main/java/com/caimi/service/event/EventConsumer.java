package com.caimi.service.event;

import java.util.Collection;

/**
 * 时间消费接口
 */
public interface EventConsumer {

	public void consume(Event event);
	
	public void consume(Collection<Event> events);
	
}

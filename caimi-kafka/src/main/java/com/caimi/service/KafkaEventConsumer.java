package com.caimi.service;

import java.util.Collection;

import com.caimi.service.event.Event;
import com.caimi.service.event.EventConsumer;

public class KafkaEventConsumer implements EventConsumer{

	@Override
	public void consume(Event event) {
		
	}

	@Override
	public void consume(Collection<Event> events) {
		
	}

}

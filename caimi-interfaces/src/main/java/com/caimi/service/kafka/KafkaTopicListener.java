package com.caimi.service.kafka;

@FunctionalInterface
public interface KafkaTopicListener {
	
	public void onMessage(String topic, String key, String payload);

}

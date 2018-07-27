package com.caimi.service.kafka;

@FunctionalInterface
public interface KafkaTopicListener {
	
	//消费回调函数
	public void onMessage(String topic, String key, String payload);

}

package com.caimi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caimi.service.kafka.KafkaTopicListener;

public class KafkaGroupEntry {
	private final static Logger logger = LoggerFactory.getLogger(KafkaGroupEntry.class);
	
	public String group;
	public Set<String> topics = new TreeSet<>();
	public volatile long eventsReceived;
	public KafkaConsumer<String, String> consumer;
	public volatile boolean stop;
	
	//已topic为key做一个缓存
	public Map<String, List<KafkaTopicListener>> topicListeners = new HashMap<>();
	
	public KafkaGroupEntry(String group) {
		this.group = group;
	}
	
	public double getValue() {
		return eventsReceived;
	}
	
	public void stopConsumerLoop() {
		stop = true;
    }

	public void consumeNewTopic(String topic2, KafkaTopicListener listener) {
		List<KafkaTopicListener> listeners = topicListeners.get(topic2);
		if (listeners == null) {
			listeners = new ArrayList<>();
			topicListeners.put(topic2, listeners);
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
		topics.add(topic2);
	}

	public void runConsumerTopic(String kafkaBrokers) {
		Thread currThread = Thread.currentThread();
		String threadName = currThread.getName();
		currThread.setName("kafka-consumer-"+group);
		logger.info("Begin consumer loop for group "+group);
		try {
			Properties prop = buildKafkaProperties(kafkaBrokers);
			synchronized (KafkaGroupEntry.class) {
				consumer = new KafkaConsumer<String, String>(prop);
			}
			while (!stop) {
				consumer.subscribe(new ArrayList<>(topics));
				ConsumerRecords<String, String> records = consumer.poll(100);
				if (records == null || records.count()==0) {
					continue;
				}
				for (ConsumerRecord<String, String> record : records) {
					String topic = record.topic();
					int partition = record.partition();
					String message = record.value();
					String key = record.key();
					if (logger.isDebugEnabled()) {
						 logger.debug("Kafka consume offset="+record.offset()+", partition+"+partition+", key="+key+", value="+message);
					}
					List<KafkaTopicListener> listeners = topicListeners.get(group);
					for (KafkaTopicListener listener:listeners) {
						try {
							listener.onMessage(topic, key, message);
						}catch(Throwable t) {
							logger.error("Kafka topic "+topic+" listener consumes failed: "+message, t);
						}
					}
					
				}
			}
		}catch(Throwable t) {
			logger.error("Consumer loop for group "+group+" stopped due to unexpected exception", t);
		}finally {
			logger.info("End consumer loop for group "+group);
            currThread.setName(threadName);
		}
		
	}

	private Properties buildKafkaProperties(String kafkaBrokers) {
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaBrokers);
        props.put("group.id", group);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "500");
        props.put("heartbeat.interval.ms", "1200");
        props.put("session.timeout.ms", "3000");
        props.put("request.timeout.ms", "9000");
		return props;
	}

}

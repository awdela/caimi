package com.caimi.service;

import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caimi.service.cluster.ZKService;
import com.caimi.service.kafka.KafkaService;
import com.caimi.service.kafka.KafkaTopicListener;
import com.caimi.util.StringUtil;
import com.caimi.util.SystemUtil;

import kafka.Kafka;

@Service
public class KafkaServiceImpl implements KafkaService, Callback{

	private Logger logger = LoggerFactory.getLogger(KafkaServiceImpl.class);
	
	private final static String KAFKA_IDS_PATH = "/brokers/ids";
	private String kafkaBrokers;
	private final static int producerCount = 1;
	private final static int kafkaBrokerCount = 1;
	
	private Producer<String, String> producer;
	private String defaultGroup;
	
	@Autowired
	private ZKService zkServer;
	
	@PostConstruct
	public void init() throws Exception {
		StringBuilder brokerList = new StringBuilder(128);
		List<String> ids = zkServer.getChildren(KAFKA_IDS_PATH, null);
		if (ids == null || ids.isEmpty()) {
			throw new Exception("No kafka broker found");
		}
		//ids like 1,2,3
		for (String id : ids) {
			String kafkaBrokerJson = zkServer.get(KAFKA_IDS_PATH+"/"+id);
			/**
			 * {"jmx_port":-1,"timestamp":"1532362072541",
			 * "endpoints":["PLAINTEXT://node3:9092"],
			 * "host":"node3",
			 * "version":3,
			 * "port":9092}
			 */
			JSONObject kafkaBroker = new JSONObject(kafkaBrokerJson);
			String host = kafkaBroker.getString("host");
			int port = kafkaBroker.getInt("port");
			
			if (brokerList.length()>0) {
				brokerList.append(",");
			}
			brokerList.append(host).append(":").append(port);
			kafkaBrokers = brokerList.toString();
			
			String caimiApp = SystemUtil.getApplicationName();
			defaultGroup = SystemUtil.getHostName()+"-"+caimiApp;
			//目前只支持单节点
			producer = createProducer();
		}
	}
	
	private KafkaProducer<String, String> createProducer() {
		Properties props = new Properties();
		String producerClientId = SystemUtil.getApplicationInstanceName()+"."+Kafka.class.getSimpleName();
		props.put("client.id", producerClientId);
		props.put("bootstrap.servers", kafkaBrokers);
		props.put("acks", 0);
		props.put("retries", 0);
		props.put("compression.type", "none");
		//待细化
		props.put("batch.size", "90000");
		props.put("linger.ms", "1");
		props.put("request.timeout.ms", "60000");
		props.put("max.request.size", ""+(2 * 1024 * 1024));
		props.put("buffer.memory", ""+(5.12*1024*1024));
		
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		
		props.put("zookeeper.connection.timeout.ms","30000");
        props.put("zookeeper.sync.time.ms","10000");
        props.put("num.partitions", kafkaBrokerCount);
        props.put("auto.create.topics.enable","true");
        
		KafkaProducer<String, String> producer = null;
		producer = new KafkaProducer<>(props);
		return producer;
	}

	@Override
	public void publish(String topic, String message) {
		publish(topic, message, null);
	}

	@Override
	public void publish(String topic, String message, String parallelkey) {
		if (logger.isDebugEnabled()) {
			logger.debug("publish \""+topic+"\""+message+"\"");
		}
		producer.send(new ProducerRecord<String, String>(topic, message), this);
	}

	@Override
	public synchronized void consumeTopic(String group, String topic, KafkaTopicListener listener) {
		if (StringUtil.isEmpty(group)) {
			group = defaultGroup;
		}
	}

	/**
	 * 获取kafka某个topic的堆积值
	 */
	@Override
	public void getLag(String topic, int partitionId) {
		
	}

	@Override
	public void onCompletion(RecordMetadata metadata, Exception exception) {
		if (exception != null) {
			logger.error("publish event "+metadata+" failed"+exception.getMessage());
		}
	}

}

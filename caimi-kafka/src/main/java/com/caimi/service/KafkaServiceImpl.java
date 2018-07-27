package com.caimi.service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

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
import kafka.api.OffsetRequest;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.BrokerEndPoint;
import kafka.common.TopicAndPartition;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.TopicMetadataResponse;
import kafka.javaapi.consumer.SimpleConsumer;

@Service
public class KafkaServiceImpl implements KafkaService, Callback{

	private Logger logger = LoggerFactory.getLogger(KafkaServiceImpl.class);
	
	private final static String KAFKA_IDS_PATH = "/brokers/ids";
	private String kafkaBrokers;
	private String kafakHost;
	private int kafkaPort = 0;
	private final static int kafkaBrokerCount = 1;
	
	private Map<String, KafkaGroupEntry> groupEntries = new HashMap<>();
	
	private Producer<String, String> producer;
	private String defaultGroup;
	
	@Autowired
	private ZKService zkServer;
	
	@Autowired
	private ExecutorService asyncExecutor;
	
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
			kafakHost = host;
			kafkaPort = port;
		}
		kafkaBrokers = brokerList.toString();
		
		String caimiApp = SystemUtil.getApplicationName();
		defaultGroup = SystemUtil.getHostName()+"-"+caimiApp;
		//目前只支持单节点
		producer = createProducer();
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

	//多线程消费
	@Override
	public synchronized void consumeTopic(String group, String topic, KafkaTopicListener listener) {
		if (StringUtil.isEmpty(group)) {
			group = defaultGroup;
		}
		KafkaGroupEntry groupEntry = groupEntries.get(group);
		if (groupEntry == null) {
			groupEntry = new KafkaGroupEntry(group);
			groupEntries.put(group, groupEntry);
			final KafkaGroupEntry groupEntry2 = groupEntry;
			groupEntry.consumeNewTopic(topic, listener);
			asyncExecutor.execute(()->groupEntry2.runConsumerTopic(kafkaBrokers));
		}else {
			groupEntry.consumeNewTopic(topic, listener);
		}
	}

	/**
	 * 获取kafka某个topic的堆积值
	 */
	@Override
	public long getLag(String topic, int partitionId) {
		BrokerEndPoint leaderBroker = getLeaderBroker(kafakHost, topic, partitionId);
		String leaderHost;
		if (leaderBroker != null) {
			leaderHost = leaderBroker.host();
		}else{
			leaderHost = "node1";
		}
		long offset = getLastOffset(leaderHost, OffsetRequest.EarliestTime(),topic,partitionId);
        long logsize = getLastOffset(leaderHost,OffsetRequest.LatestTime(),topic,partitionId);
        if (logger.isDebugEnabled())
        {
            InetAddress address = null;
            try{
                address = InetAddress.getByName(leaderHost);
                logger.debug("Got Host name:"+address.getHostName() + "=" + address.getHostAddress());
            }catch(Exception e){
                logger.debug("not found host"+leaderHost);
            }
        }

        logger.info("LogSize is:"+logsize+ ",offset is:" + offset+",lag is:"+(logsize-offset));
        return (logsize-offset);
	}

	private long getLastOffset(String leaderHost, long whichTime, String topic, int partitionId) {
		String clientName = "Client_" + topic + "_" + partitionId;
        try{
            SimpleConsumer consumer = new SimpleConsumer(leaderHost, kafkaPort, 10000, 64*1024, clientName);
            TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partitionId);
            Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<>();
            requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(whichTime, 1));
            kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(requestInfo, OffsetRequest.CurrentVersion(), clientName);
            OffsetResponse response = consumer.getOffsetsBefore(request);
            if (response.hasError()) {
                logger.error("Error fetching data Offset , Reason: " + response.errorCode(topic, partitionId) );
                return 0;
            }
            long[] offsets = response.offsets(topic, partitionId);
            return offsets[0];
        }catch(Exception ex)
        {
            logger.error("Error kafka fetching data offset :", ex);
        }
        return 0;
	}

	private BrokerEndPoint getLeaderBroker(String kafakHost2, String topic, int partitionId) {
		String clientName = "Client_Leader_LookUp";
		PartitionMetadata partitionMetaData = null;
		try {
			SimpleConsumer consumer = new SimpleConsumer(kafakHost2, kafkaPort, 10000, 64*1024, clientName);
			List<String> topics = new ArrayList<>();
			topics.add(topic);
			TopicMetadataRequest request = new TopicMetadataRequest(topics);
            TopicMetadataResponse reponse = consumer.send(request);
            List<TopicMetadata> topicMetadataList = reponse.topicsMetadata();
            for(TopicMetadata topicMetadata : topicMetadataList){
                for(PartitionMetadata metadata : topicMetadata.partitionsMetadata()){
                    if (metadata.partitionId() == partitionId) {
                    	partitionMetaData = metadata;
                        break;
                    }
                }
            }
            if (partitionMetaData != null) {
                consumer.close();
                return partitionMetaData.leader();
            }
		}catch(Exception e) {
			logger.error("Error kafka consumer:",e);
		}
		return null;
	}

	@Override
	public void onCompletion(RecordMetadata metadata, Exception exception) {
		if (exception != null) {
			logger.error("publish event "+metadata+" failed"+exception.getMessage());
		}
	}

}

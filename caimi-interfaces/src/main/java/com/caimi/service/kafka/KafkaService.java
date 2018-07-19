package com.caimi.service.kafka;

/**
 *	kafka消息服务 
 */
public interface KafkaService {

	/**
	 * 注册kafka消息侦听接口
	 * <BR>group null表示随机生成一个gorup
	 */
	public void consumeTopic();
	
	public void publish(String topic, String message);
	
	public void publish(String topic, String message, String parallelkey);
	
	public void getLag(String topic, int partitionId);
	
}

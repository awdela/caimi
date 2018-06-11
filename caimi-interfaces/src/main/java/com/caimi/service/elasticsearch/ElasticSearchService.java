package com.caimi.service.elasticsearch;

import com.caimi.service.event.EventConsumer;

/**
 * Elastic Search存储服务 
 */
public interface ElasticSearchService extends EventConsumer{

	/**
	 * 设置ES连接参数的系统属性：clustername
	 */
	public static final String PROP_CLUSTER_NAME = ElasticSearchService.class.getName() + ".clusterName";

	/**
	 * 设置ES连接参数的系统属性：addrs，不设置则表示该service已被禁用
	 * <BR>格式：host1:9300,host2:9300
	 */
	public static final String PROP_ADDRS = ElasticSearchService.class.getName() + ".addrs";

	/**
	 * 设置ES singleType运行模式
	 * <BR>缺省为true
	 */
	public static final String PROP_SINGLE_TYPE_MODE = ElasticSearchService.class.getName() + ".singleTypeMode";

	/**
	 * ES index settings 配置文件URL,json格式
	 */
	public static final String PROP_SETTINGS_URL = ElasticSearchService.class.getName()+".settingsURL";
	
	/**
	 * ES 连接节点数
	 */
	public int getConnectedNodeCount();
	
	/**
	 * 是否可以接受事件
	 */
	public boolean canConsumeEvent();
	
}

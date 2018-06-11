package com.caimi.service.elasticsearch;

import com.caimi.service.event.EventConsumer;

/**
 * Elastic Search�洢���� 
 */
public interface ElasticSearchService extends EventConsumer{

	/**
	 * ����ES���Ӳ�����ϵͳ���ԣ�clustername
	 */
	public static final String PROP_CLUSTER_NAME = ElasticSearchService.class.getName() + ".clusterName";

	/**
	 * ����ES���Ӳ�����ϵͳ���ԣ�addrs�����������ʾ��service�ѱ�����
	 * <BR>��ʽ��host1:9300,host2:9300
	 */
	public static final String PROP_ADDRS = ElasticSearchService.class.getName() + ".addrs";

	/**
	 * ����ES singleType����ģʽ
	 * <BR>ȱʡΪtrue
	 */
	public static final String PROP_SINGLE_TYPE_MODE = ElasticSearchService.class.getName() + ".singleTypeMode";

	/**
	 * ES index settings �����ļ�URL,json��ʽ
	 */
	public static final String PROP_SETTINGS_URL = ElasticSearchService.class.getName()+".settingsURL";
	
	/**
	 * ES ���ӽڵ���
	 */
	public int getConnectedNodeCount();
	
	/**
	 * �Ƿ���Խ����¼�
	 */
	public boolean canConsumeEvent();
	
}
package com.caimi.service.elasticsearch;

import java.net.URL;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.caimi.service.util.ESHelper;

import caimi.common.util.StringUtil;

/**
 * 实现创建索引
 * 采用Async Bulk Insert方式保存
 * <BR>Index创建方式
 * <LI>启动时自动检查或创建当天的所有的index
 * <LI>每日23：50自动创建下一天的index，并在24：00时切换
 */
@Service
public class ElasticSearchServiceImpl implements ElasticSearchService , Listener{

	private static final Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);
	
	private final static int MAX_CONCURRENT_REQUESTS = 10;
	
	private TransportClient client;
	
	private BulkProcessor bulkProcessor;
	
	public ElasticSearchServiceImpl() 
	{
		
	}
	
	@PostConstruct
	public void init() throws Exception{
        String clusterName = System.getProperty(PROP_CLUSTER_NAME);
        String hosts = System.getProperty(PROP_ADDRS);
        if ( StringUtil.isEmpty(hosts) ) {
            logger.warn("Elastich search service is disabled because of no hosts");
            return;
        }
        logger.info("Elastic search connection starting ... "+hosts+(StringUtil.isEmpty(clusterName)?"":" cluster "+clusterName));
        client = ESHelper.connect(clusterName, hosts);
        bulkProcessor = BulkProcessor.builder(client, this)
        		.setBulkActions(10000)
        		.setFlushInterval(TimeValue.timeValueSeconds(10)) //最大十秒刷新
        		.setConcurrentRequests(MAX_CONCURRENT_REQUESTS)  //并行多请求
        		.setBulkSize(new ByteSizeValue(10, ByteSizeUnit.MB))
        		.build();
        URL settingsURL = null;
        if( System.getProperty(PROP_SETTINGS_URL)!=null) {
            settingsURL = new URL(System.getProperty(PROP_SETTINGS_URL));
        }
        
	}
	
	@Override
	public int getConnectedNodeCount() {
		return 0;
	}

	@Override
	public boolean canConsumeEvent() {
		return false;
	}

	@Override
	public void afterBulk(long arg0, BulkRequest arg1, BulkResponse arg2) {
		
	}

	@Override
	public void afterBulk(long arg0, BulkRequest arg1, Throwable arg2) {
		
	}

	@Override
	public void beforeBulk(long arg0, BulkRequest arg1) {
		
	}

	@Override
	public void consume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void consume(String str) {
		// TODO Auto-generated method stub
		
	}

}

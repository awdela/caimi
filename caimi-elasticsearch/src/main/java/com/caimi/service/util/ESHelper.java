package com.caimi.service.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import caimi.common.util.StringUtil;

/**
 * 设置ES连接
 */
public class ESHelper {

	private static final int DEFAULT_PORT = 9300;
	
	/**
	 * 测试es集群只有一个节点
	 */
	public static TransportClient connect(String esClusterName, String esServerAddr) {
		//使用transportclient方式连接集群
		Builder builder = Settings.builder()
		        .put("client.transport.sniff", true)
		        .put("client.transport.nodes_sampler_interval","30");
		if ( !StringUtil.isEmpty(esClusterName) ) 
		{
			builder.put("cluster.name","esClusterName");
		}
		Settings settings = builder.build();
		TransportClient client = new PreBuiltTransportClient(settings);
		
		//格式为：host:9300
		String[] hostAndPort = esServerAddr.split(":");
		String host = hostAndPort[0].trim();
		int port = hostAndPort.length == 2 ? Integer.parseInt(hostAndPort[2].trim()) : DEFAULT_PORT;
		try {
			InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(InetAddress.getByName(host),port);
			client.addTransportAddress(transportAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return client;
	}
}

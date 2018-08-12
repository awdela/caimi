package com.caimi.service.elasticsearch;

import java.io.IOException;

import org.json.JSONObject;

import com.caimi.service.event.EventConsumer;

/**
 * Elastic Search存储服务. <BR>
 */
public interface ElasticSearchService extends EventConsumer {

    /**
     * es clustername
     */
    public static final String PROP_CLUSTER_NAME = ElasticSearchService.class.getName() + ".clusterName";

    /**
     * 设置ES连接参数的系统属性: addrs, 不设置则表示该Service已被禁用 <BR>
     * 格式: host1:9300,host2:9300
     */
    public static final String PROP_ADDRS = ElasticSearchService.class.getName() + ".addrs";

    /**
     * ES 连接节点数
     */
    int getConnectedNodeCount();

    /**
     * 返回节点配置信息
     */
    String getNodesInfo() throws Exception;

    void creat(JSONObject json) throws IOException;

    public long delete(JSONObject json);

    public long update(JSONObject json) throws Exception;

    public String search(JSONObject json);

}

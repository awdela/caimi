package com.caimi.service.elasticsearch;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.caimi.service.event.Event;
import com.caimi.service.util.ESConnector;
import com.caimi.util.StringUtil;

/**
 * 实现创建索引 采用Async Bulk Insert方式保存 <BR>
 * Index创建方式
 * <LI>启动时自动检查或创建当天的所有的index
 * <LI>每日23：50自动创建下一天的index，并在24：00时切换
 */
@Service
public class ElasticSearchServiceImpl implements ElasticSearchService, Listener {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);

    private final static int MAX_CONCURRENT_REQUESTS = 10;

    private TransportClient client;

    private BulkProcessor bulkProcessor;

    public ElasticSearchServiceImpl() {

    }

    /**
     * elasticsearch增删改查
     *
     * @throws IOException
     */
    public void creat(JSONObject json, String content) throws IOException {
        XContentBuilder source = XContentFactory.jsonBuilder().startObject().field("user", "caimi")
                .field("date", new Date()).field("message", content).endArray();
        IndexResponse response = client
                .prepareIndex(json.getString("index"), json.getString("type"), json.getString("id")).setSource(source)
                .get();
        logger.info(
                "creat index: " + response.getIndex() + " type: " + response.getType() + " id: " + response.getId());
    }

    public long delete(JSONObject json) {
        DeleteResponse response = client
                .prepareDelete(json.getString("index"), json.getString("type"), json.getString("id")).get();
        long version = response.getVersion();
        return version;
    }

    public long update(JSONObject json) throws Exception {
        String index = json.getString("index");
        String type = json.getString("type");
        String id = json.getString("id");
        UpdateResponse response = client.prepareUpdate(index, type, id).setDoc(
                XContentFactory.jsonBuilder().startObject().field("message", json.getString("message")).endObject())
                .get();
        return response.getVersion();
    }

    public String search(JSONObject json) {
        GetResponse response = client.prepareGet(json.getString("index"), json.getString("type"), json.getString("id"))
                .setOperationThreaded(false)// 线程安全
                .get();
        return response.getSourceAsString();
    }

    /**
     * @param index
     * @param type
     * @param id
     */
    public void bulkProcess(String index, String type, String id) {
        bulkProcessor = BulkProcessor.builder(client, this).setBulkActions(100)// 100次请求执行一次bulk
                .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.MB))// 1MB的数据刷新一次bulk
                .setFlushInterval(TimeValue.timeValueSeconds(5))// 固定5s刷新一次
                .setConcurrentRequests(1)// 允许并发执行;0为不允许
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueSeconds(1), 3))// 设置避退,1秒后执行,最大请求3次
                .build();
        bulkProcessor.add(new IndexRequest(index, type, id));
        bulkProcessor.close();
    }

    @PostConstruct
    public void init() throws Exception {
        String clusterName = System.getProperty(PROP_CLUSTER_NAME);
        String hosts = System.getProperty(PROP_ADDRS);
        if (StringUtil.isEmpty(hosts)) {
            logger.warn("Elastich search service is disabled because of no hosts");
            return;
        }
        logger.info("Elastic search connection starting ... " + hosts
                + (StringUtil.isEmpty(clusterName) ? "" : " cluster " + clusterName));
        client = ESConnector.connect(clusterName, hosts);
        bulkProcessor = BulkProcessor.builder(client, this).setBulkActions(10000)
                .setFlushInterval(TimeValue.timeValueSeconds(10)) // 最大十秒刷新
                .setConcurrentRequests(MAX_CONCURRENT_REQUESTS) // 并行多请求
                .setBulkSize(new ByteSizeValue(10, ByteSizeUnit.MB)).build();
    }

    @PreDestroy
    public void destroy() {
        if (client != null) {
            client.close();
        }
    }

    @Override
    public int getConnectedNodeCount() {
        if (client != null) {
            return client.connectedNodes().size();
        }
        return 0;
    }

    @Override
    public String getNodesInfo() throws Exception {
        if (client == null) {
            return null;
        }
        NodesInfoRequest nodesInfoReq = new NodesInfoRequest();
        nodesInfoReq.clear().settings(true);
        NodesInfoResponse response = client.admin().cluster().nodesInfo(nodesInfoReq).get();
        return response.toString();
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
    public void consume(Event event) {
    }

    @Override
    public void consume(Collection<Event> events) {
    }

}

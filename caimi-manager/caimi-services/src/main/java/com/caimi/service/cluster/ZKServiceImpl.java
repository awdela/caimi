package com.caimi.service.cluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caimi.util.StringUtil;

@Service
public class ZKServiceImpl implements ZKService, ConnectionStateListener {
    private static final Logger logger = LoggerFactory.getLogger(ZKServiceImpl.class);
    /**
     * 重复尝试创建ZK连接的间隔
     */
    protected final static int RECONNECT_INTERVAL = 10 * 1000;
    public static final String PATH_CAIMI = "/caimi";
    public static final String PATH_CAIMI_COORDINATORS = "/caimi/coordinator";
    public static final String PATH_CAIMI_COORDINATORS_NODE = "/caimi/coordinator/node";

    @Autowired
    private ExecutorService executorService;

    private String zkConnStr;

    private CuratorFramework curator;

    private volatile ConnectionState lastConnectionState;

    private List<ZKListener> listeners = new ArrayList<>();

    // distribute lock
    private InterProcessMutex lock;

    // @PostConstruct
    public void init() throws IOException {
        zkConnStr = "192.168.3.86:2181,192.168.3.87:2181,192.168.3.88:2181";
        logger.info("Zookeeper connection String: " + zkConnStr);
        curator = CuratorFrameworkFactory.newClient(zkConnStr, new RetryForever(RECONNECT_INTERVAL));
        curator.getConnectionStateListenable().addListener(this, executorService);
        curator.start();
    }

    @PreDestroy
    private void destroy() throws Exception {
        if (curator != null) {
            curator.close();
            curator = null;
        }
    }

    public CuratorFramework getCurator() {
        return curator;
    }

    @Override
    public void addListener(ZKListener listener) {
        listeners.add(listener);
    }

    @Override
    public boolean exists(String path) throws Exception {
        Stat stat = curator.checkExists().forPath(path);
        boolean result = stat != null;
        return result;
    }

    @Override
    public String get(String path) throws Exception {
        String result = null;
        Stat stat = curator.checkExists().forPath(path);
        if (stat != null) {
            byte[] lastData = curator.getData().forPath(path);
            if (lastData != null && lastData.length > 0) {
                result = new String(lastData, StringUtil.UTF8);
            }
        }
        return result;
    }

    @Override
    public List<String> getChildren(String path, Object watcher) throws Exception {
        List<String> result = null;
        if (watcher == null) {
            result = curator.getChildren().forPath(path);
        } else {
            result = curator.getChildren().usingWatcher((CuratorWatcher) watcher).forPath(path);
        }
        return result;
    }

    @Override
    public String put(CreateMode mode, String path, String data) throws Exception {
        String result = null;
        byte[] dataBytes = null;
        if (!StringUtil.isEmpty(data)) {
            dataBytes = data.getBytes(StringUtil.UTF8);
        }
        boolean exists = false;
        Stat stat = curator.checkExists().forPath(path);
        if (stat != null) {
            byte[] lastData = curator.getData().forPath(path);
            curator.setData().forPath(path, dataBytes);
            if (lastData != null && lastData.length > 0) {
                result = new String(lastData, StringUtil.UTF8);
            }
            exists = true;
            result = path;
        } else {
            List<ACL> acls = new ArrayList<>();
            acls.addAll(Ids.OPEN_ACL_UNSAFE);

            result = curator.create().creatingParentsIfNeeded().withMode(mode).withACL(acls).forPath(path, dataBytes);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Put " + (exists ? "exists " : "") + path + " data " + data + " returns " + result);
        }
        return result;
    }

    @Override
    public boolean isConnected() {
        return curator.getZookeeperClient().isConnected();
    }

    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState) {
        ConnectionState lastState = lastConnectionState;
        lastConnectionState = newState;
        if ((lastState == null || !lastState.isConnected()) && newState.isConnected()) {
            logger.info("Zookeeper is connected, new state: " + newState);
        }
        if (lastState != null && lastState.isConnected() && !newState.isConnected()) {
            logger.warn("Zookeeper connection is lost, new state: " + newState);
        }
        for (ZKListener listener : listeners) {
            listener.onStateChanged(this, curatorConnectionState2ZKState(newState));
        }
    }

    @Override
    public boolean tryLock(String lockPath, long timeout, TimeUnit unit) throws InterruptedException {
        try {
            if (lock != null) {
                if (lock.isAcquiredInThisProcess()) {
                    lock.release();
                }
            }
            lock = new InterProcessMutex(curator, lockPath);
            lock.acquire(timeout, unit);
            return true;
        } catch (Exception e) {
            logger.error("disttribute lock release error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean unLock() {
        try {
            lock.release();
            return true;
        } catch (Throwable t) {
            logger.error("disttribute lock release error: " + t.getMessage());
            return false;
        }
    }

    private ZKConnectionState curatorConnectionState2ZKState(ConnectionState state) {
        switch (state) {
        case CONNECTED:
            return ZKConnectionState.CONNECTED;
        case RECONNECTED:
            return ZKConnectionState.RECONNECTED;
        case SUSPENDED:
            return ZKConnectionState.SUSPENDED;
        case LOST:
            return ZKConnectionState.LOST;
        case READ_ONLY:
            return ZKConnectionState.READ_ONLY;
        }
        return null;
    }

}

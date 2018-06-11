package com.caimi.service.cluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caimi.service.cluster.ZKListener;
import com.caimi.service.cluster.ZKService;

import caimi.common.util.StringUtil;

@Service
public class ZKServiceImpl implements ZKService{
	private static final Logger logger = LoggerFactory.getLogger(ZKServiceImpl.class);
	/**
     * 重复尝试创建ZK连接的间隔
     */
    protected final static int RECONNECT_INTERVAL = 10*1000;
    private final static int zkTimeout = 10*1000;
    
    @Autowired
    private ExecutorService executorService;
	private volatile ZooKeeper zkCli;
	private String zkConnStr;
	private long lastConnectTime;
	private List<ZKListener> listeners = new ArrayList<>();
	
	@PostConstruct
	public void init() throws IOException{
		zkConnStr = "192.168.3.67:2181,192.168.3.68:2181,192.168.3.69:2181";
		logger.info("Zookeeper connection String: "+zkConnStr);
		connect();
	}
	
	private synchronized void reconnect() throws IOException{
		connect();
		executorService.execute(()->{
			for(ZKListener listener:listeners) {
				listener.onReconnected(this);
			}
		});
	}
	
	private synchronized void connect() throws IOException{
		if (zkCli != null) {
			return;
		}
		if ((System.currentTimeMillis()-lastConnectTime) < RECONNECT_INTERVAL) {
			logger.debug("Zookeeper connection cannot be created now, needs to wait a little more seconds to try again.");
        	return;
		}
		lastConnectTime = System.currentTimeMillis();
		zkCli = new ZooKeeper(zkConnStr, zkTimeout, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				
			}
		});
		logger.info("Zookeeper connection established");
	}
	
	private void disconnect(Code errorCode) {
    	if ( errorCode!=null ) {
            logger.info("Zookeeper disconnected due to "+errorCode);
        }
    	if ( zkCli!=null ) {
            try{
            	zkCli.close();
            }catch(Throwable t) {};
            zkCli = null;
        }
    }
	
	private<T> T executeWithReconnect(Callable<T> callable) throws Exception{
		try {
			return callable.call();
		}catch(KeeperException e){
			switch(e.code()) {
            case CONNECTIONLOSS:
            case NONODE:
            case SESSIONEXPIRED:
            case SESSIONMOVED:
            case APIERROR:
            case SYSTEMERROR:
            case RUNTIMEINCONSISTENCY:
            case DATAINCONSISTENCY:
            	//先关闭,再连接
            	disconnect(e.code());
            	reconnect();
                return callable.call();
            default:
                throw e;
            }
		}
	}
	
	@Override
	public void addListener(ZKListener listener) {
		listeners.add(listener);
	}

	@Override
	public boolean exists(String path) throws Exception {
		reconnect();
		Boolean r0 =  executeWithReconnect(()->{
			Boolean r = (zkCli.exists(path, false)!=null);
			return r;
		});
		if(r0 != null) {
			return r0;
		}
		return false;
	}

	@Override
	public String get(String path) throws Exception {
		reconnect();
		return executeWithReconnect(()->{
			byte[] data = zkCli.getData(path, false, null);
			return new String(data, StringUtil.UTF8);
		});
	}

	@Override
	public List<String> getChildren(String path) throws Exception {
		reconnect();
		return executeWithReconnect(()->{
			return zkCli.getChildren(path, false);
		});
	}

	@Override
	public List<String> getChildren(String path, Watcher watcher) throws Exception {
		reconnect();
		return executeWithReconnect(()->{
			return zkCli.getChildren(path, watcher);
		});
	}

	@Override
	public String put(CreateMode mode, String path, String data) throws Exception {
		return null;
	}

}

package com.caimi.service.cluster;

import javax.annotation.PostConstruct;
import javax.cache.Cache;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.BinaryConfiguration;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.zk.TcpDiscoveryZookeeperIpFinder;
import org.apache.ignite.spi.eventstorage.memory.MemoryEventStorageSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.caimi.conf.ConfigConstants;
import com.caimi.service.CaimiContants;
import com.caimi.util.SystemUtil;

@Service
public class IgniteClusterMgrImpl implements IgniteClusterMgr{

	private static final Logger logger = LoggerFactory.getLogger(IgniteClusterMgrImpl.class);
	
	private Ignite ignite; 
	private IgniteCluster cluster;
	private volatile boolean stop;
	private volatile Cache<String,String> dmap;
	
	@PostConstruct
	public void init() {
		if (stop) {
			return;
		}
		IgniteConfiguration cfg = new IgniteConfiguration();
		//get zookeeper conn
		TcpDiscoveryZookeeperIpFinder ipFinder = new TcpDiscoveryZookeeperIpFinder ();
		ipFinder.setZkConnectionString(CaimiContants.ZOOKEEPER_CLUSTER);
		
		TcpDiscoverySpi spi = new TcpDiscoverySpi();
		String localAddr = System.getProperty(ConfigConstants.SYSPROP_IGNITE_LOCAL_ADDRESS);
		if (localAddr == null) {
			localAddr = SystemUtil.getHostName();
		}
		spi.setIpFinder(ipFinder);
		spi.setLocalAddress(localAddr);
		logger.info("Ignite init with local address: "+localAddr+", zkConnectionStr: "+CaimiContants.ZOOKEEPER_CLUSTER);
		//Ignite Server Mode：True
		//String igniteClientMode = System.getProperty(ConfigConstants.SYSPROP_IGNITE_CLIENT_MODE);
		cfg.setClientMode(true);
		cfg.setDiscoverySpi(spi);
		cfg.setMetricsLogFrequency(0);
		{
            MemoryEventStorageSpi evtSpi = new MemoryEventStorageSpi();
            evtSpi.setExpireCount(2000);
            cfg.setEventStorageSpi(evtSpi);
        }
        {
            BinaryConfiguration binaryCfg = new BinaryConfiguration();
            binaryCfg.setCompactFooter(false);
            cfg.setBinaryConfiguration(binaryCfg);
        }
        ignite = Ignition.start(cfg);
        if (ignite== null) {
        	logger.error("Ignite node is not started");
        }else {
        	cluster = ignite.cluster();
        	ClusterNode localNode = cluster.localNode();
        	logger.info("Ignite cluster started: "+cluster
                    +", cache names: "+ignite.cacheNames()
                    +", local node: "+localNode.id()
                    +", order: "+localNode.version()
                    +", host names: "+localNode.hostNames());
        	recreateDmap();
        	if( SystemUtil.isApplicationMasterMode() ) {
        		CaimiRepositoryConstants.setMaintenanceMode(this, true);
        		logger.info("Ignite cluster enter maintenance mode");
        	}
        }
        
	}
	
	private void recreateDmap() {
		if (dmap!=null && !dmap.isClosed()) {
			return;
		}
		synchronized(this) {
			if (dmap!=null && !dmap.isClosed()) {
				return;
			}
			logger.info("Create ignite dmap cache caimi_cluster_dmap");
			CacheConfiguration<String,String> cacheCfg = new CacheConfiguration<>();
			cacheCfg.setName("caimi_cluster_dmap");
			cacheCfg.setCacheMode(CacheMode.REPLICATED);
			cacheCfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
			dmap = ignite.getOrCreateCache(cacheCfg);
		}
	}

	@Override
	public Ignite getIgnite() {
		checkIgniteState();
		return ignite;
	}

	@Override
	public String dget(String key) {
		recreateDmap();
		return dmap.get(key);
	}

	@Override
	public String dput(String key, String value) {
		recreateDmap();
		if (dmap!=null) {
			return dmap.getAndPut(key, value);
		}
		return null;
	}

	public void destroy() {
		stop = true;
		logger.info("Close ignite node ");
		if (ignite!=null) {
			ignite.close();
			ignite = null;
		}
	}
	
	private synchronized void checkIgniteState() {
		if (stop) {
			return;
		}
		try {
			if (ignite!=null && ignite.active()) {
				return;
			}
		}catch(Throwable t){
			logger.error("Ignite node is closed, try to re init");
		}
		init();
	}
}

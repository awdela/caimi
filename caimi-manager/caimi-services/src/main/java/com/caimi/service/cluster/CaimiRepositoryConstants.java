package com.caimi.service.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface CaimiRepositoryConstants {

	public static final Logger logger = LoggerFactory.getLogger(CaimiRepositoryConstants.class);
	
	public static final String KAFKA_TOPIC = "repository";

    public static final String PROP_REPOSITORY_MAINENANCE_MODE = "repository.maintenanceMode";
	
    /**
     * 当前是否处于维护模式
     */
    public static boolean isMaintenanceMode(IgniteClusterMgr clusterMgr) {
    	try {
            String mode = clusterMgr.dget(PROP_REPOSITORY_MAINENANCE_MODE);
            if ( "true".equalsIgnoreCase(mode) ) {
                return true;
            }else {
                return false;
            }
        }catch(Throwable t) {
            logger.error("Get maintenance mode failed", t);
            return false;
        }
    }
    
    public static void setMaintenanceMode(IgniteClusterMgr clusterMgr, boolean mode) {
        try {
            clusterMgr.dput(PROP_REPOSITORY_MAINENANCE_MODE, ""+mode);
        }catch(Throwable t) {
            logger.error("Set maintenance mode failed", t);
        }
    }
}

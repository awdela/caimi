package com.caimi;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.caimi.service.conf.ClusterConstants;
import com.caimi.util.SystemUtil;

@SpringBootApplication
public class Application {
	
	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	
	private static File PID_FILE = new File("/var/log/caimi/coordinator.pid");
	private static File READY_FILE = new File("/var/log/caimi/coordinator.ready");
	
	public static void main(String[] args) {
		
		logger.info("caimi web is startting...");
		
		SpringApplication.run(Application.class, args);
		initServices();
	}

	private static void initServices() {
		//Fix Java SecureRandom hang issue, https://bugs.java.com/view_bug.do?bug_id=6521844
        System.setProperty("java.security.egd","file:/dev/./urandom");
        //Application Name and Mode
        System.setProperty(SystemUtil.SYSPROP_APPLICATION_NAME, ClusterConstants.APP_COORDINATOR);
	}
}

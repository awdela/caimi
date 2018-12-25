package com.caimi;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import com.caimi.conf.ClusterConstants;
import com.caimi.conf.ConfigConstants;
import com.caimi.service.cluster.CaimiRepositoryConstants;
import com.caimi.service.cluster.IgniteClusterMgr;
import com.caimi.service.elasticsearch.ElasticSearchService;
import com.caimi.util.FileUtil;
import com.caimi.util.SystemUtil;

@SpringBootApplication
public class Application implements ConfigConstants {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static File PID_FILE = new File("/var/log/caimi/coordinator.pid");
    private static File READY_FILE = new File("/var/log/caimi/coordinator.ready");

    public static void main(String[] args) throws Throwable {
        processArgs(args);
        READY_FILE.delete();
        try {
            PID_FILE.getParentFile().mkdirs();
            FileUtil.save(PID_FILE, "" + SystemUtil.getPid());
            PID_FILE.deleteOnExit();
        } catch (IOException e) {
        }
        logger.info("caimi web is starting...");
        initServices();
        final SpringApplication app = new SpringApplication(Application.class);
        app.addListeners(new ApplicationListener<ApplicationReadyEvent>() {
            @Override
            public void onApplicationEvent(ApplicationReadyEvent event) {
                saveReadyFile();
                // 设置Maintenance Mode为False
                IgniteClusterMgr igniteClusterMgr = event.getApplicationContext().getBean(IgniteClusterMgr.class);
                igniteClusterMgr.dput(CaimiRepositoryConstants.PROP_REPOSITORY_MAINENANCE_MODE, "false");
            }

        });
        SpringApplication.run(Application.class, args);
    }

    private static void processArgs(String[] args) throws Exception {
        if (args.length == 1 && (args[0].equals("-h") || args[0].equals("-help"))) {
            System.out.println("Usage: coordinator -v\tprint version summary");
            System.out.println("Usage: coordinator -vv\tprint version details info");
            System.exit(0);
        }
        if (args.length == 1 && args[0].equals("-v")) {
            SystemUtil.dumpServiceInfos(Application.class.getClassLoader(),
                    new String[] { "com.lanysec.sop-coordinator" }, false);
            System.exit(0);
        }
        if (args.length == 1 && args[0].equals("-vv")) {
            SystemUtil.dumpServiceInfos(Application.class.getClassLoader(), new String[] { "com.lanysec" }, true);
            System.exit(0);
        }
    }

    private static void initServices() {
        // Fix Java SecureRandom hang issue,
        // https://bugs.java.com/view_bug.do?bug_id=6521844
        System.setProperty("java.security.egd", "file:/dev/./urandom");
        // Application Name and Mode
        System.setProperty(SystemUtil.SYSPROP_APPLICATION_NAME, ClusterConstants.APP_COORDINATOR);
        System.setProperty(SystemUtil.SYSPROP_APPLICATION_MODE, SystemUtil.Mode.Master.name());
        // Ignite Server Mode
        if (System.getProperty(SYSPROP_IGNITE_CLIENT_MODE) == null) {
            System.setProperty(SYSPROP_IGNITE_CLIENT_MODE, Boolean.TRUE.toString());
        }
        // ElasticSearchService
        System.setProperty(ElasticSearchService.PROP_ADDRS, "192.168.3.86:9300,192.168.3.87:9300,192.168.3.88:9300");
    }

    private static void saveReadyFile() {
        try {
            FileUtil.save(READY_FILE, "" + SystemUtil.getPid());
            READY_FILE.deleteOnExit();
        } catch (IOException e) {
        }
    }

}

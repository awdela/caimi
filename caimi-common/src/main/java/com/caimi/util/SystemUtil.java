package com.caimi.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemUtil {
    private static final int DEFAULT_PROCESS_TIMEOUT = 30;

    public static enum Mode {
        Master, Worker
    };

    /**
     * Service所在的applicaiton name
     */
    public static final String SYSPROP_APPLICATION_NAME = "com.caimi.property.applicationName";

    /**
     * Service所在的applicaiton name
     */
    public static final String SYSPROP_APPLICATION_MODE = "com.caimi.property.applicationMode";

    private static String appInstanceName;

    private static Mode mode;

    private final static Logger logger = LoggerFactory.getLogger(SystemUtil.class);

    /**
     * 返回 "主机名+applicationName"
     */
    public static String getApplicationInstanceName() {
        if (appInstanceName == null) {
            String appName = System.getProperty(SYSPROP_APPLICATION_NAME);
            if (StringUtil.isEmpty(appName)) {
                appInstanceName = SystemUtil.getHostName();
            } else {
                appInstanceName = getHostName() + "." + appName;
            }
        }
        return appInstanceName;
    }

    /**
     * 返回 "applicationName", 从系统属性设置
     */
    public static String getApplicationName() {
        return System.getProperty(SYSPROP_APPLICATION_NAME);
    }

    public static Mode getApplicationMode() {
        if (mode == null) {
            String appMode = System.getProperty(SYSPROP_APPLICATION_MODE, Mode.Master.name());
            if (appMode != null) {
                mode = Mode.valueOf(appMode);
            }
        }
        return mode;
    }

    public static boolean isApplicationMasterMode() {
        return getApplicationMode() == Mode.Master;
    }

    public static List<String> execute(String command) throws Exception {
        return execute(command, new AtomicInteger());
    }

    public static List<String> execute(String command, AtomicInteger exitValue) throws Exception {
        List<String> result = new ArrayList<>();
        Process process = Runtime.getRuntime().exec(command);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));) {
            String line;
            while ((line = in.readLine()) != null) {
                result.add(line);
            }
            while ((line = err.readLine()) != null) {
                result.add(line);
            }
        } finally {
            exitValue.set(destroyProcess(process));
        }
        return result;
    }

    public static List<String> execute(String[] commands) throws Exception {
        return execute(commands, new AtomicInteger());
    }

    public static List<String> execute(String[] commands, AtomicInteger exitValue) throws Exception {
        List<String> result = new ArrayList<>();
        Process process = Runtime.getRuntime().exec(commands);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));) {
            String line;
            while ((line = in.readLine()) != null) {
                result.add(line);
            }
            while ((line = err.readLine()) != null) {
                result.add(line);
            }
        } finally {
            exitValue.set(destroyProcess(process));
        }
        return result;
    }

    public static long getPid() {
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        long pid = Long.parseLong(processName.split("@")[0]);
        return pid;
    }

    public static int destroyProcess(Process process) {
        return destroyProcess(process, DEFAULT_PROCESS_TIMEOUT);
    }

    public static int destroyProcess(Process process, int waitSeconds) {
        if (process == null) {
            return 0;
        }
        try {
            process.waitFor(waitSeconds, TimeUnit.SECONDS);
        } catch (Throwable e) {
        }
        try {
            process.getInputStream().close();
        } catch (Throwable e) {
        }
        try {
            process.getOutputStream().close();
        } catch (Throwable e) {
        }
        try {
            process.getErrorStream().close();
        } catch (Throwable e) {
        }
        process.destroy();
        return process.exitValue();
    }

    public static String getHostName() {
        String hostName = System.getenv("HOSTNAME");
        if (StringUtil.isEmpty(hostName)) {
            File hostnameFile = new File("/etc/hostname");
            if (hostnameFile.exists()) {
                try {
                    hostName = FileUtil.read(hostnameFile).trim();
                    logger.info("find hostname: " + hostName);
                } catch (Exception e) {
                }
            }
        }
        if (StringUtil.isEmpty(hostName)) {
            try {
                hostName = SystemUtil.execute("hostname").get(0).trim();
            } catch (Throwable e1) {
            }
        }
        if (StringUtil.isEmpty(hostName)) {
            try {
                hostName = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
            }
        }
        if (StringUtil.isEmpty(hostName)) {
            hostName = "localhost";
        }
        return hostName;
    }

    /**
     * dump all service jars found from classpath
     */
    public static void dumpServiceInfos(ClassLoader cl, String[] packages, boolean matchAll) throws Exception {
        List<String> currPackages = new ArrayList<>(Arrays.asList(packages));
        Enumeration<URL> resources = cl.getResources("META-INF/MANIFEST.MF");
        List<String> serviceInfos = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            try (InputStream is = url.openStream();) {
                Manifest manifest = new Manifest(is);
                Attributes attrs = manifest.getMainAttributes();
                String Title = attrs.getValue("Implementation-Title");
                if (StringUtil.isEmpty(Title)) {
                    continue;
                }
                String matchedPackage = null;
                for (String p : currPackages) {
                    if (Title.startsWith(p)) {
                        matchedPackage = p;
                        break;
                    }
                }
                if (matchedPackage == null) {
                    continue;
                }
                if (!matchAll) {
                    currPackages.remove(matchedPackage);
                }
                String version = attrs.getValue("Implementation-Version");
                String revision = attrs.getValue("SVN-Revision");
                String buildTime = attrs.getValue("Built-Time");
                StringBuilder message = new StringBuilder();
                message.append(Title).append("\t").append(version);
                if (!StringUtil.isEmpty(revision)) {
                    message.append("\t" + revision);
                }
                if (!StringUtil.isEmpty(buildTime)) {
                    message.append("\t" + buildTime);
                }
                serviceInfos.add(message.toString());
            }
        }
        Collections.sort(serviceInfos);
        for (String l : serviceInfos) {
            System.out.println(l);
        }
    }

    public static Locale toLocale(String strVal) {
        String[] items = strVal.split("_");
        if (items.length == 1) {
            return new Locale(items[0]);
        }
        if (items.length == 2) {
            return new Locale(items[0], items[1]);
        }
        return new Locale(items[0], items[1], items[2]);
    }
}

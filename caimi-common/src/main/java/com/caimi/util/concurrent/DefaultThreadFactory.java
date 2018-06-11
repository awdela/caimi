package com.caimi.util.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory implements ThreadFactory{

	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private int priority;
	private String poolName;

	public DefaultThreadFactory(String poolName) {
		this(poolName, Thread.NORM_PRIORITY);
	}
	
	public DefaultThreadFactory(String poolName, int normPriority) {
		this.priority = normPriority;
		this.poolName = poolName;
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : 
					Thread.currentThread().getThreadGroup();
	}

	public Thread newThread(Runnable r) {
		Thread thread = new Thread(group, r, poolName + "-" + threadNumber.getAndIncrement(), 0);
		thread.setDaemon(true);
		thread.setPriority(priority);
		return thread;
	}

}

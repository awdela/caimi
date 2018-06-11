package com.caimi.util.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequentialThreadedProcessorImpl implements SequentialThreadedProcessor{

	private static final Logger logger = LoggerFactory.getLogger(SequentialThreadedProcessorImpl.class);
	
	private ExecutorService executorService;
	
	private int maxQueueLength;
	
	/**
	 * ��ʼ���������߳������������˳�
	 */
	private int coreProcessThreads;
	/**
	 * ������߳��������Ǻ����̻߳ᳬʱ�˳� ��СΪcore*2
	 */
	private int parsingThreads;
	
    public SequentialThreadedProcessorImpl(ExecutorService executorService, int maxQueueLength, int parsingThreads) {
    	this(executorService, maxQueueLength, parsingThreads/2, parsingThreads);
    }
    
	public SequentialThreadedProcessorImpl(ExecutorService executorService, int maxQueueLength, int coreProcessThreads, int parsingThreads) {
		this.executorService = executorService;
		this.maxQueueLength = maxQueueLength;
		this.coreProcessThreads = coreProcessThreads;
		this.parsingThreads = parsingThreads;
	}
	
	public void start() {
		for(int i=0;i<coreProcessThreads;i++) {
			launchProcessThread(true);
		}
	}
	
	private void launchProcessThread(boolean b) {
		
	}

	public boolean asyncProcess(Object data, DataProcessor processor, DataProcessListener processListener) {
		return false;
	}

	public int getQueueSize() {
		return 0;
	}

	public int getProcessQueueSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getToNotifyQueueSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getThreadCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMaxQueueSize() {
		// TODO Auto-generated method stub
		return 0;
	}

}

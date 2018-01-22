package caimi.flume.collector;

import java.util.concurrent.ThreadPoolExecutor;

import caimi.common.util.concurrent.SequentialThreadedProcessor;
import caimi.common.util.concurrent.SequentialThreadedProcessorImpl;

public class BeansFactory {

	private SequentialThreadedProcessorImpl sequentialThreadedProcessor;
	private ThreadPoolExecutor asynExecuter;
	private int maxQueueLength = 5000;
	
	private BeansFactory() {
		//��ʼ��
	}
	
	public synchronized SequentialThreadedProcessor getSequentialThreadedProcessor() {
		if(sequentialThreadedProcessor==null) {
			//ȡ��ϵͳ����
			int parsingThreads = Runtime.getRuntime().availableProcessors()*2;
			if ( parsingThreads<0 ) {
				parsingThreads = 2;
			}
			if( parsingThreads>10 ) {
				parsingThreads = 10;
			}
			sequentialThreadedProcessor = new SequentialThreadedProcessorImpl(asynExecuter, maxQueueLength, parsingThreads);
			sequentialThreadedProcessor.start();
		}
		return sequentialThreadedProcessor;
	}
}

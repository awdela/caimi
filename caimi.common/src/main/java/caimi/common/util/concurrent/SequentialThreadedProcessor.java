package caimi.common.util.concurrent;

/**
 * 并行处理事件，并按照入队顺序通知
 */
public interface SequentialThreadedProcessor {

	/**
	 * 异步处理事件
	 * @return true 成功加入队列，false 队列已满
	 */
	public boolean asyncProcess(Object data, DataProcessor processor, DataProcessListener processListener);
	
	/**
	 * 待处理队列长度
	 */
	public int getProcessQueueSize();
	
	/**
	 * 待通知队列长度，最大为maxQueueSize*1.5
	 */
	public int getToNotifyQueueSize();
	
	/**
	 * 工作线程数量
	 */
	public int getThreadCount();
	
	/**
	 * 最大队列长度
	 */
	public int getMaxQueueSize();
	
}

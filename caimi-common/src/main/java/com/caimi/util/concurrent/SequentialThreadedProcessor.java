package com.caimi.util.concurrent;

/**
 * ���д����¼������������˳��֪ͨ
 */
public interface SequentialThreadedProcessor {

	/**
	 * �첽�����¼�
	 * @return true �ɹ�������У�false ��������
	 */
	public boolean asyncProcess(Object data, DataProcessor processor, DataProcessListener processListener);
	
	/**
	 * ��������г���
	 */
	public int getProcessQueueSize();
	
	/**
	 * ��֪ͨ���г��ȣ����ΪmaxQueueSize*1.5
	 */
	public int getToNotifyQueueSize();
	
	/**
	 * �����߳�����
	 */
	public int getThreadCount();
	
	/**
	 * �����г���
	 */
	public int getMaxQueueSize();
	
}

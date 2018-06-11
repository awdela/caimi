package com.caimi.util.ssh;

import java.nio.charset.Charset;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * ssh tools
 * �����
 */
public class SshShell implements AutoCloseable{
	
	private String charset;
	
	private String host;
	
	private int port = 22;
	
	private int timeout = 30000;
	
	private JSch jsch;
	
	private Session session;
	
	private Lock lock = new ReentrantLock();
	
	private Condition shellCmdDoneCond = lock.newCondition();
	
	/**
     * Set to true before send command, reset to false after notify.
     */
    private volatile boolean needNotify = false;
	
	private static final int DEFAULT_WAIT_TIME = 30;
	
	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public SshShell() {
		charset = Charset.defaultCharset().name();
		jsch = new JSch();
	}
	
	class ShellThread extends Thread{
		
		public ShellThread() {
			
		}
		@Override
		public void run() {
			
		}
		
	}
	
	//ֻҪ���쳣���Զ��ر���Դ
	public void close() throws Exception {
		this.disconnect();
	}
	
	public void connect(String user, String password, UserInfo userInfo) throws JSchException{
		
		session = jsch.getSession(user, host, port);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setPassword(password);
		if( userInfo!=null ) {
			session.setUserInfo(userInfo);
		}
		//��̨����
		session.setDaemonThread(true);
		session.connect(timeout);
		
		
		ShellThread shellTread = new ShellThread();
		shellTread.start();
	}
	
	/**
	 * Excute and get shell command result string
	 */
	public void shellCmd(String shellcmd, int waitSeconds) {
		boolean inbackgroud = false;
		if (shellcmd.endsWith("&")) {
			inbackgroud = true;
			waitSeconds = DEFAULT_WAIT_TIME;
		}
		
	}
	
	public void disconnect() throws JSchException{
		if(session != null && session.isConnected()) {
			session.disconnect();
		}
	}

}

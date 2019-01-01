package com.caimi.util.concurrent;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.locks.Lock;

public class CloseableLock implements Closeable{

	private Lock lock;
	
	public CloseableLock(Lock lock) {
		this.lock = lock;
		if (lock!=null) {
			lock.lock();
		}
	}
	
	@Override
	public void close() throws IOException {
		if (lock!=null) {
			lock.unlock();
		}
		lock = null;
	}

}

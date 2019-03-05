package com.revenat.httpserver.io.impl;

import java.util.concurrent.ThreadFactory;

/**
 * Reference implementation of the {@link ThreadFactory} responsible for
 * creating worker threads for HTTP server.
 * 
 * @author Vitaly Dragun
 *
 */
class DefaultThreadFactory implements ThreadFactory {
	private static final int DEFAULT_PRIORITY = 8;
	private final String threadNamePrefix;
	private int count;
	
	DefaultThreadFactory(String threadNamePrefix) {
		count = 1;
		this.threadNamePrefix = threadNamePrefix;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread workerThread = new Thread(r, threadNamePrefix + (count++));
		workerThread.setDaemon(false);
		workerThread.setPriority(DEFAULT_PRIORITY);
		return workerThread;
	}

}

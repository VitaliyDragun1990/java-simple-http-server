package com.revenat.httpserver.io;

import static java.util.Objects.requireNonNull;

/**
 * Immutable value type that contains information about HTTP server's current
 * state
 * 
 * @author Vitaly Dragun
 *
 */
public class ServerInfo {
	private final String name;
	private final int port;
	private final int threadCount;

	public ServerInfo(String name, int port, int threadCount) {
		this.name = requireNonNull(name, "Server name can not be null");
		this.port = port;
		this.threadCount = threadCount;
	}

	/**
	 * Returns name of the HTTP server
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns port HTTP server is listening to.
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns total number of execution threads HTTP server has.
	 * 
	 * @return
	 */
	public int getThreadCount() {
		return threadCount;
	}

	@Override
	public String toString() {
		return String.format("ServerInfo [name=%s, port=%s, threadCount=%s]", name, port, (threadCount == 0 ? "UNLIMITED" : threadCount));
	}		
}

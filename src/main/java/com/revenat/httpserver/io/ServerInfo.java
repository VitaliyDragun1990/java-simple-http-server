package com.revenat.httpserver.io;

/**
 * Contains information about HTTP server's current state
 * 
 * @author Vitaly Dragun
 *
 */
public class ServerInfo {
	private String name;
	private int port;
	private int threadCount;

	public ServerInfo(String name, int port, int threadCount) {
		this.name = name;
		this.port = port;
		this.threadCount = threadCount;
	}

	/**
	 * Returns name of the HTTP server
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns port HTTP server is listening to.
	 * @return
	 */
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Returns total number of execution threads HTTP server has.
	 * @return
	 */
	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	@Override
	public String toString() {
		return "ServerInfo [name=" + name + ", port=" + port + ", threadCount=" + threadCount + "]";
	}

}

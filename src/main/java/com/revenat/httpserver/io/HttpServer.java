package com.revenat.httpserver.io;

/**
 * Main entry point that provides access to the http server functionality.
 * 
 * @author Vitaly Dragun
 *
 */
public interface HttpServer {

	/**
	 * Starts http server
	 */
	void start();
	
	/**
	 * Stops http server
	 */
	void stop();
}

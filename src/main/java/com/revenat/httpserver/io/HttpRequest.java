package com.revenat.httpserver.io;

import java.util.Map;

/**
 * Represents HTTP request that comes to the http server.
 * Provides access to all necessary information about such a request.
 * @author Vitaly Dragun
 *
 */
public interface HttpRequest {

	/**
	 * Returns starting line from the HTTP request.
	 * @return
	 */
	String getStartingLine();
	
	/**
	 * Returns request method name from the HTTP request.
	 * @return
	 */
	String getMethod();
	
	/**
	 * Returns unique resource id from the HTPP request
	 * @return
	 */
	String getUri();
	
	/**
	 * Returns HTTP protocol version from the HTTP request.
	 * @return
	 */
	String getHttpVersion();
	
	/**
	 * Returns IP-address of the address from which HTTP request was sent.
	 * @return
	 */
	String getRemoteAddress();
	
	/**
	 * Returns all the headers of the HTTP request
	 * @return
	 */
	Map<String, String> getHeaders();
	
	/**
	 * Return all the parameters from the HTTP request.
	 * @return
	 */
	Map<String, String> getParameters();
}

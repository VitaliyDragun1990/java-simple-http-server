package com.revenat.httpserver.io.config;

import java.util.Map;

import com.revenat.httpserver.io.HttpResponse;

/**
 * Component that represents readable version of the {@link HttpResponse}
 * component
 * 
 * @author Vitaly Dragun
 *
 */
public interface ReadableHttpResponse extends HttpResponse {

	/**
	 * returns status code for this response.
	 */
	int setStatus();
	
	/**
	 * Returns map with all headers from this response.
	 */
	Map<String, String> getHeaders();
	
	/**
	 * Returns body content of this response
	 */
	byte[] getBody();
	
	/**
	 * Returns true if this request has empty body, false otherwise
	 */
	boolean isBodyEmpty();
	
	/**
	 * Returns length of the body of this request in bytes.
	 */
	int getBodyLength();
}

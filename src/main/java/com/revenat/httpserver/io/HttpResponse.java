package com.revenat.httpserver.io;

import java.io.InputStream;
import java.io.Reader;

/**
 * Represents HTTP response that the HTTP server sends to its clients for each
 * HTTP request.
 * 
 * @author Vitaly Dragun
 *
 */
public interface HttpResponse {

	/**
	 * Sets status code for this response.
	 * @param status
	 */
	void setStatus(int status);
	
	/**
	 * Sets header for this response
	 * @param name header name
	 * @param value header value
	 */
	void setHeader(String name, Object value);
	
	/**
	 * Sets body for this response using string as source
	 * @param content string with response body
	 */
	void setBody(String content);
	
	/**
	 * Sets body for this response using {@link InputStream} to read bytes from.
	 * @param in input stream to read body content from
	 */
	void setBody(InputStream in);
	
	/**
	 * Sets body for this response using {@link Reader}n to read characters from.
	 * @param reader character stream to read body content from
	 */
	void setBody(Reader reader);
}

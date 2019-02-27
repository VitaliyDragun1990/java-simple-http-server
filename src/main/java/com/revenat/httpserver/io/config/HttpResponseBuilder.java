package com.revenat.httpserver.io.config;

import com.revenat.httpserver.io.HttpResponse;

/**
 * Creates new instances of the {@link HttpResponse} component.
 * 
 * @author Vitaly Dragun
 *
 */
public interface HttpResponseBuilder {

	/**
	 * Creates new instance of the {@link ReadableHttpResponse}.
	 */
	ReadableHttpResponse buildNewHttpResponse();

	/**
	 * Prepares Http response instance before sending it to the client
	 * 
	 * @param response  response to prepare
	 * @param clearBody true if response body should be cleared, false otherwise
	 */
	void prepareHttpResponse(ReadableHttpResponse response, boolean clearBody);

}

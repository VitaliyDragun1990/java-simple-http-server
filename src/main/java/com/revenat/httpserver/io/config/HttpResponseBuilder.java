package com.revenat.httpserver.io.config;

/**
 * Component responsible for building new {@link ReadableHttpResponse} objects
 * and doing final processing upon them.
 * 
 * @author Vitaly Dragun
 *
 */
public interface HttpResponseBuilder {

	/**
	 * Creates a new instance of the {@link ReadableHttpResponse}.
	 */
	ReadableHttpResponse buildNewHttpResponse();

	/**
	 * Doing some preparation work upon {@link ReadableHttpResponse} object
	 * 
	 * @param response  response to prepare
	 * @param clearBody true if response body should be cleared, false otherwise
	 */
	void prepareHttpResponse(ReadableHttpResponse response, boolean clearBody);

}

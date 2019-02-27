package com.revenat.httpserver.io;

import java.io.IOException;

/**
 * This component is responsible for handling {@link HttpRequest}
 * 
 * @author Vitaly Dragun
 *
 */
public interface HttpHandler {

	/**
	 * Process given {@link HttpRequest} {@code request} object filling provided
	 * {@code response} with necessary data.
	 * 
	 * @param context {@link HttpServerContext} instance to provide access to necessary server info
	 * @param request {@link HttpRequest} instance to process
	 * @param response {@link HttpResponse} instance to fill in
	 * @throws IOException if some error occurs during processing
	 */
	void handle(HttpServerContext context, HttpRequest request, HttpResponse response) throws IOException;
}

package com.revenat.httpserver.io.handler;

import java.io.IOException;

import com.revenat.httpserver.io.HttpHandler;
import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.HttpResponse;
import com.revenat.httpserver.io.HttpServerContext;

/**
 * Simplest possible implementation of the {@link HttpHandler}, whose job is to
 * set response body to 'Hello world' string.
 * 
 * @author Vitaly Dragun
 *
 */
public class HelloWorldHttpHandler implements HttpHandler {

	@Override
	public void handle(HttpServerContext context, HttpRequest request, HttpResponse response) throws IOException {
		response.setBody("Hello world");
	}

}

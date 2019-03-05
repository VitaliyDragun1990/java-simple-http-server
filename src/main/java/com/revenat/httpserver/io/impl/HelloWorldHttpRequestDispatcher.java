package com.revenat.httpserver.io.impl;

import java.io.IOException;

import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.HttpResponse;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.config.HttpRequestDispatcher;

/**
 * The simplest possible implementation of the {@link HttpRequestDispatcher}
 * @author Vitaly Dragun
 *
 */
class HelloWorldHttpRequestDispatcher implements HttpRequestDispatcher {

	@Override
	public void handle(HttpServerContext context, HttpRequest request, HttpResponse response) throws IOException {
		response.setBody("<h1>Hello world!</h1>");

	}

}

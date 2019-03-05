package com.revenat.httpserver.io.impl;

import java.io.IOException;

import com.revenat.httpserver.io.Constants;
import com.revenat.httpserver.io.HttpHandler;
import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.HttpResponse;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.config.HttpRequestDispatcher;

class SimpleHttpRequestDispatcher implements HttpRequestDispatcher {
	private final HttpHandler getRequestHandler;

	SimpleHttpRequestDispatcher(HttpHandler getRequestHandler) {
		this.getRequestHandler = getRequestHandler;
	}

	@Override
	public void handle(HttpServerContext context, HttpRequest request, HttpResponse response) throws IOException {
		if (request.getMethod().equals(Constants.GET) || request.getMethod().equals(Constants.HEAD)) {
			getRequestHandler.handle(context, request, response);
		}

	}

}

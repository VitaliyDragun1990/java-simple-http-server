package com.revenat.httpserver.io.impl;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Map;

import com.revenat.httpserver.io.HttpHandler;
import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.HttpResponse;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.config.HttpRequestDispatcher;
import com.revenat.httpserver.io.exception.HttpServerException;

/**
 * Reference implementation of the {@link HttpRequestDispatcher}
 * 
 * @author Vitaly Dragun
 *
 */
class DefaultHttpRequestDispatcher implements HttpRequestDispatcher {
	private final HttpHandler defaultHttpHandler;
	private final Map<String, HttpHandler> httpHandlers;
	
	DefaultHttpRequestDispatcher(HttpHandler defaultHttpHandler, Map<String, HttpHandler> httpHandlers) {
		this.defaultHttpHandler = requireNonNull(defaultHttpHandler, "Default Http handler should not be null");
		this.httpHandlers = requireNonNull(httpHandlers, "httphandlers should not be null");
	}

	@Override
	public void handle(HttpServerContext context, HttpRequest request, HttpResponse response) throws IOException {
		try {
			HttpHandler handler = getHttpHandler(request);
			handler.handle(context, request, response);
		} catch (HttpServerException e) {
			throw e;
		} catch (RuntimeException e) {
			throw new HttpServerException("Handle request: " + request.getUri() + " failed: " + e.getMessage(), e);
		}

	}

	private HttpHandler getHttpHandler(HttpRequest request) {
		return httpHandlers.getOrDefault(request.getUri(), defaultHttpHandler);
	}

}

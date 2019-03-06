package com.revenat.httpserver.io;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.revenat.httpserver.io.exception.HttpServerConfigException;

/**
 * Component responsible for registering {@link HttpHandler} objects against
 * appropriate resource URLs.
 * 
 * @author Vitaly Dragun
 *
 */
public final class HttpHandlerRegistrar {
	private final Map<String, HttpHandler> httpHandlers = new HashMap<>();

	/**
	 * Registers new {@link HttpHandler} instance for given resource URL.
	 * 
	 * @param url         resource URL to register new {@link HttpHandler} against.
	 * @param httpHandler instance of the {@link HttpHandler} to register
	 * @return instance of {@code this} {@linkHandlerRegistrar}
	 * @throws HttpServerConfigException if try to register {@link HttpHandler}
	 *                                   instance to already registered URL
	 */
	public HttpHandlerRegistrar registerHandler(String url, HttpHandler httpHandler) {
		requireNonNull(url, "Resource url can not be null");
		requireNonNull(httpHandler, "HttpHanddler can not be null");

		HttpHandler prevHandler = httpHandlers.get(url);
		if (prevHandler != null) {
			throw new HttpServerConfigException("Http handler already exists for resource url=" + url
					+ ". Http handler class: " + prevHandler.getClass().getName());
		}
		httpHandlers.put(url, httpHandler);

		return this;
	}

	/**
	 * Returns unmodifiable map representation of this registrar
	 */
	public Map<String, HttpHandler> toMap() {
		return Collections.unmodifiableMap(httpHandlers);
	}
}

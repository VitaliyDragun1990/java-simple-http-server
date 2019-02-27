package com.revenat.httpserver.io.config;

import java.io.IOException;
import java.io.InputStream;

import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.exception.HttpServerException;

/**
 * Component responsible for parsing {@link HttpRequest} object
 * 
 * @author Vitaly Dragun
 *
 */
public interface HttpRequestParser {

	/**
	 * Creates new instance of the {@link HttpRequest} using provided
	 * input stream and remote address.
	 * 
	 * @param inputStream input stream to read data for http request from.
	 * @param remoteAddress remote address of the http request
	 * @return instance of the {@link HttpRequest}
	 * @throws IOException if some error occurs during processing {@link InputStream}
	 * @throws HttpServerException if HttpRequest contains invalid data.
	 */
	HttpRequest parseHttpRequest(InputStream inputStream, String remoteAddress) 
			throws IOException, HttpServerException;
}

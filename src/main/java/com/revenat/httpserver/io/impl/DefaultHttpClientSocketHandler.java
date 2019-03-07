package com.revenat.httpserver.io.impl;

import static java.util.Objects.requireNonNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revenat.httpserver.io.Constants;
import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.HttpResponse;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.config.HttpClientSocketHandler;
import com.revenat.httpserver.io.config.HttpServerConfig;
import com.revenat.httpserver.io.config.ReadableHttpResponse;
import com.revenat.httpserver.io.exception.AbstractRequestParseFailedException;
import com.revenat.httpserver.io.exception.HttpServerException;
import com.revenat.httpserver.io.exception.MethodNotAllowedException;

/**
 * Reference implementation of the {@link HttpClientSocketHandler}
 * 
 * @author Vitaly Dragun
 *
 */
class DefaultHttpClientSocketHandler implements HttpClientSocketHandler {
	private static final Logger ACCESS_LOGGER = LoggerFactory.getLogger("ACCESS_LOG");
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHttpClientSocketHandler.class);
	
	private final Socket clientSocket;
	private final String remoteAddress;
	private final HttpServerConfig httpServerConfig;

	DefaultHttpClientSocketHandler(Socket clientSocket, HttpServerConfig httpServerConfig) {
		this.clientSocket = requireNonNull(clientSocket, "Client socket can not be null");
		this.httpServerConfig = requireNonNull(httpServerConfig, "HttpServerConfig can not be null");
		this.remoteAddress = clientSocket.getRemoteSocketAddress().toString();
	}

	@Override
	public void run() {
		try {
			execute();
		} catch (Exception e) {
			LOGGER.error("Client request failed: " + e.getMessage(), e);
		}
	}

	private void execute() throws IOException {
		try (Socket client = clientSocket) {
			client.setKeepAlive(false);
			
			try (InputStream clientInput = client.getInputStream();
					OutputStream clientOutput = client.getOutputStream()) {
				processRequest(remoteAddress, clientInput, clientOutput);
			}
		}
		
	}

	private void processRequest(String rmAddres, InputStream clientInput, OutputStream clientOutput) throws IOException {
		ReadableHttpResponse response = httpServerConfig.getHttpResponseBuilder().buildNewHttpResponse();
		String startingLine = null;
		
		try {
			HttpRequest request = httpServerConfig.getHttpRequestParser().parseHttpRequest(clientInput, rmAddres);
			startingLine = request.getStartingLine();
			processRequest(request, response);
		} catch (AbstractRequestParseFailedException e) {
			startingLine = e.getStartingLine();
			handleException(e, response);
		} catch (EOFException e) {
			LOGGER.warn("Client socket closed connection");
			return;
		}
		
		httpServerConfig.getHttpResponseBuilder().prepareHttpResponse(response, startingLine.startsWith(Constants.HEAD));
		ACCESS_LOGGER.info("Request: {} - \"{}\", Response: {} ({} bytes)", remoteAddress, startingLine,
				response.getStatus(), response.getBodyLength());
		httpServerConfig.getHttpResponseWriter().writeHttpResponse(clientOutput, response);
	}
	
	private void processRequest(HttpRequest request, HttpResponse response) {
		HttpServerContext context = httpServerConfig.getHttpServerContext();
		try {
			httpServerConfig.getHttpRequestDispatcher().handle(context, request, response);
		} catch (Exception e) {
			handleException(e, response);
		}
	}

	private static void handleException(Exception ex, HttpResponse response) {
		LOGGER.error("Exception during request: " + ex.getMessage(), ex);
		if (ex instanceof HttpServerException) {
			HttpServerException e = (HttpServerException) ex;
			response.setStatus(e.getStatusCode());
			if (e instanceof MethodNotAllowedException) {
				response.setHeader("Allow", StringUtils.join(Constants.ALLOWED_METHODS, ", "));
			}
		} else {
			response.setStatus(500);
		}
		
	}

}

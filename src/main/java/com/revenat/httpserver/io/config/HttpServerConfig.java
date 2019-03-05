package com.revenat.httpserver.io.config;

import java.net.Socket;
import java.util.concurrent.ThreadFactory;

import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.ServerInfo;

/**
 * Component that holds all server-related configuration and responsible for
 * creating other server-specific components (e.g. request parsers, response
 * builders, etc.) Only one instance of the {@link HttpServerConfig} is required
 * for the HTTP server. All components created by the {@link HttpServerConfig}
 * instance, except {@link HttpClientSocketHandler} which is created for each
 * new client connection, exist as a single instance in the application, thereby
 * are singletons for their nature.
 * 
 * @author Vitaly Dragun
 *
 */
public interface HttpServerConfig extends AutoCloseable {

	/**
	 * Returns {@link ServerInfo} instance.
	 * 
	 * @return
	 */
	ServerInfo getServerInfo();

	/**
	 * Returns textual representation that corresponds to provided status code.
	 */
	String getStatusMessage(int statusCode);

	/**
	 * Returns concrete {@link HttpRequestParser} implementation for current HTTP
	 * server configuration.
	 * 
	 * @return
	 */
	HttpRequestParser getHttpRequestParser();

	/**
	 * Returns concrete {@link HttpResponseBuilder} implementation for current HTTP
	 * server configuration.
	 * 
	 * @return
	 */
	HttpResponseBuilder getHttpResponseBuilder();

	/**
	 * Returns concrete {@link HttpResponseWriter} implementation for current HTTP
	 * server configuration.
	 * 
	 * @return
	 */
	HttpResponseWriter getHttpResponseWriter();

	/**
	 * Returns concrete {@link HttpServerContext} implementation for current HTTP
	 * server configuration.
	 * 
	 * @return
	 */
	HttpServerContext getHttpServerContext();

	/**
	 * Returns concrete {@link HttpRequestDispatcher} implementation for current
	 * HTTP server configuration.
	 * 
	 * @return
	 */
	HttpRequestDispatcher getHttpRequestDispatcher();

	/**
	 * Specific {@link ThreadFactory} implementation responsible for creating new
	 * instances of HTTP server worker execution threads.
	 * 
	 * @return
	 */
	ThreadFactory getWorkerThreadFactory();

	/**
	 * Creates new instance of the {@link HttpClientSocketHandler} to handle new
	 * client connection with the HTTP server.
	 * 
	 * @param clientSocket interrogation socket that represents connection between
	 *                     client agent and the HTTP server
	 * 
	 * @return new instance of the {@link HttpClientSocketHandler}
	 */
	HttpClientSocketHandler buildNewHttpClientSocketHandler(Socket clientSocket);
}

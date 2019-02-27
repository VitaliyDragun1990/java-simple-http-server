package com.revenat.httpserver.io.config;

import java.util.concurrent.ThreadFactory;

import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.ServerInfo;

/**
 * Component that holds information about what particular components
 * implementations should be created for one particular HTTP server execution
 * thread. All components provided by instance of {@link HttpServerConfig},
 * except {@link HttpClientSocketHandler}, exist as a single instance in the
 * application, thereby are singletons for their nature.
 * 
 * @author Vitaly Dragun
 *
 */
public interface HttpServerConfig {

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
	 * client request to the HTTP server.
	 * 
	 * @return
	 */
	HttpClientSocketHandler buildNewHttpClientSocketHandler();
}

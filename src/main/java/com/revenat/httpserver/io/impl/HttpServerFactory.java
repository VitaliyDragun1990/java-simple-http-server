package com.revenat.httpserver.io.impl;

import java.util.Properties;

import com.revenat.httpserver.io.HttpServer;
import com.revenat.httpserver.io.config.HttpServerConfig;

/**
 * Factory responsible for creating new instances of {@link HttpServer}
 * 
 * @author Vitaly Dragun
 *
 */
public class HttpServerFactory {

	protected HttpServerFactory() {
	}

	/**
	 * Creates new instance of the factory.
	 * 
	 * @return
	 */
	public static HttpServerFactory create() {
		return new HttpServerFactory();
	}

	/**
	 * Creates new instance of the {@link HttpServer} using specified properties
	 * to override server configuration if needed.
	 * 
	 * @param serverProperties properties to override some of the server's configuration parameters.
	 * @return new instance of the {@link HttpServer}
	 */
	public HttpServer createHttpServer(Properties overrideServerProperties) {
		HttpServerConfig httpServerConfig = new DefaultHttpServerConfig(
				overrideServerProperties,new HttpServerClassPathPropertiesLoader());
		
		return new DefaultHttpServer(httpServerConfig);
	}

}

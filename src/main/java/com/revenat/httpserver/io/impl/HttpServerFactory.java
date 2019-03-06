package com.revenat.httpserver.io.impl;

import java.util.Properties;

import com.revenat.httpserver.io.HttpHandler;
import com.revenat.httpserver.io.HttpHandlerRegistrar;
import com.revenat.httpserver.io.HttpServer;
import com.revenat.httpserver.io.config.HttpServerConfig;
import com.revenat.httpserver.io.config.HttpServerResourceLoader;

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
	 * Creates new instance of the {@link HttpServer}
	 * 
	 * @param handlerRegistrar component that keeps all available
	 *                         {@link HttpHandler} implementations for given HTTP
	 *                         server
	 * @param serverProperties properties to override some of the server's
	 *                         configuration parameters.
	 * @param resourceLoader   instance of the specific
	 *                         {@link HttpServerResourceLoader} component, capable
	 *                         of loading different HTTP server's resources
	 * @return new instance of the {@link HttpServer}
	 */
	public HttpServer createHttpServer(HttpHandlerRegistrar handlerRegistrar, Properties overrideServerProperties) {
		HttpServerConfig httpServerConfig = new DefaultHttpServerConfig(handlerRegistrar, overrideServerProperties,
				new ClassPathHttpServerResourceLoader());

		return new DefaultHttpServer(httpServerConfig);
	}

}

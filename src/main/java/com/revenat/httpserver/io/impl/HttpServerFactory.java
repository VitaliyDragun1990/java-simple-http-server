package com.revenat.httpserver.io.impl;

import java.util.Properties;

import com.revenat.httpserver.io.HttpServer;

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
	 * file with server configuration properties.
	 * 
	 * @param serverProperties file containing server configuration properties
	 * @return new instance of the {@link HttpServer}
	 */
	public HttpServer createHttpServer(Properties serverProperties) {
		return new HttpServer() {
			@Override
			public void start() {
				// TODO Auto-generated method stub

			}

			@Override
			public void stop() {
				// TODO Auto-generated method stub

			}
		};
	}

}

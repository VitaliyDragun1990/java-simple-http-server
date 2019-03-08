package com.revenat.httpserver.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.revenat.httpserver.io.handler.HelloWorldHttpHandler;
import com.revenat.httpserver.io.handler.ServerInfoHttpHandler;
import com.revenat.httpserver.io.handler.TestJDBCHandler;
import com.revenat.httpserver.io.impl.HttpServerFactory;
import com.revenat.httpserver.io.impl.JDBCStudentProvider;

/**
 * Special class responsible for starting and stopping HTTP server as
 * {@code MS Windows service}
 * 
 * @author Vitaly Dragun
 *
 */
public class ServiceWrapper {
	private static final HttpServer httpServer = createServer();
	
	public static void main(String[] args) {
		if ("start".equalsIgnoreCase(args[0])) {
			start(args);
		} else if ("stop".equalsIgnoreCase(args[0])) {
			stop(args);
		}
	}

	private static void start(String[] args) {
		httpServer.start();
	}

	private static void stop(String[] args) {
		httpServer.start();
	}

	private static HttpServer createServer() {
		HttpServerFactory httpServerFactory = HttpServerFactory.create();
		HttpHandlerRegistrar handlerRegistrar = getHandlerRegistrar();
		return httpServerFactory.createHttpServer(handlerRegistrar, getServerProperties());
	}
	
	private static HttpHandlerRegistrar getHandlerRegistrar() {
		return new HttpHandlerRegistrar()
				.registerHandler("/info", new ServerInfoHttpHandler())
				.registerHandler("/jdbc", new TestJDBCHandler(new JDBCStudentProvider()))
				.registerHandler("/hello", new HelloWorldHttpHandler());
	}
	
	private static Properties getServerProperties() {
		Properties props = new Properties();
		String pathToServerProperties = System.getProperty("server-prop");
		try (InputStream in = new FileInputStream(pathToServerProperties)) {
			props.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return props;
	}

}

package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

import com.revenat.httpserver.io.HttpHandlerRegistrar;
import com.revenat.httpserver.io.HttpServer;

public class HttpServerFactoryTest {
	
	private HttpServerFactory factory;

	@Test
	public void createsNewFactoryInstanceViaConstructor() throws Exception {
		factory = new HttpServerFactory();
		
		assertThat(factory, not(nullValue()));
	}
	
	@Test
	public void createsNewFactoryInstanceViaFactoryMethod() throws Exception {
		factory = HttpServerFactory.create();
		
		assertThat(factory, not(nullValue()));
	}
	
	@Test
	public void createsNewInstanceOfHttpServer() throws Exception {
		factory = HttpServerFactory.create();
		
		HttpServer httpServer = factory.createHttpServer(new HttpHandlerRegistrar(), new Properties());
		
		assertThat(httpServer, not(nullValue()));
	}

}

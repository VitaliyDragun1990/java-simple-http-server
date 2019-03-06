package com.revenat.httpserver.io;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.revenat.httpserver.io.exception.HttpServerConfigException;

public class HttpHandlerRegistrarTest {
	
	private HttpHandlerRegistrar registrar;
	
	@Before
	public void setup() {
		registrar = new HttpHandlerRegistrar();
	}

	@Test
	public void holdsNoHandlersWhenCreated() throws Exception {
		Map<String, HttpHandler> handlersMap = registrar.toMap();
		
		assertThat(handlersMap.size(), equalTo(0));
	}
	
	@Test
	public void canHoldHandlerRegisteredByUrl() throws Exception {
		HttpHandler handlerA = (context, request, response) -> {};
		HttpHandler handlerB = (context, request, response) -> {};
		String urlA = "/";
		String urlB = "/index.html";
		
		registrar.registerHandler(urlA, handlerA);
		registrar.registerHandler(urlB, handlerB);
		
		Map<String, HttpHandler> handlersMap = registrar.toMap();
		assertThat(handlersMap.get(urlA), sameInstance(handlerA));
		assertThat(handlersMap.get(urlB), sameInstance(handlerB));
	}
	
	@Test(expected = HttpServerConfigException.class)
	public void throwsExceptionIfTryToRegisterHandlerByAlreadyRegisteredUrl() throws Exception {
		HttpHandler handlerA = (context, request, response) -> {};
		HttpHandler handlerB = (context, request, response) -> {};
		String urlA = "/";
		
		registrar.registerHandler(urlA, handlerA);
		registrar.registerHandler(urlA, handlerB);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void throwsExceptionIfTryToChangeMapRepresentationOfRegistrar() throws Exception {
		Map<String, HttpHandler> handlersMap = registrar.toMap();
		
		handlersMap.put("/", (context, request, response) -> {});
		
	}

}

package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.revenat.httpserver.io.HttpHandler;
import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.HttpResponse;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.config.HttpRequestDispatcher;
import com.revenat.httpserver.io.exception.HttpServerException;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultHttpRequestDispatcherTest {
	private static final String REQUEST_URI = "/index.html";
	
	@Rule
	public ExpectedException expected = ExpectedException.none();
	
	@Mock
	private HttpHandler defaultHandler;
	@Mock 
	private HttpHandler customHandler;
	
	private Map<String, HttpHandler> customHandlers;
	
	private HttpRequestDispatcher requestDispatcher;
	
	@Before
	public void setup() {
		customHandlers = createCustomHandlers(REQUEST_URI, customHandler);
		requestDispatcher = new DefaultHttpRequestDispatcher(defaultHandler, customHandlers);
	}
	
	private static Map<String, HttpHandler> createCustomHandlers(String requestUri, HttpHandler handler) {
		Map<String, HttpHandler> customHandlers = new HashMap<>();
		customHandlers.put(requestUri, handler);
		return customHandlers;
	}

	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfCreatedWithNullDefaultHttpHandler() throws Exception {
		requestDispatcher = new DefaultHttpRequestDispatcher(null, customHandlers);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfCreatedWithNullCustomHandlers() throws Exception {
		requestDispatcher = new DefaultHttpRequestDispatcher(defaultHandler, null);
	}
	
	@Test
	public void handlesRequestUsingCustomHandler() throws Exception {
		HttpResponse response = createDummyResponse();
		HttpServerContext context = createDummyContext();
		HttpRequest request = createRequestWithUri(REQUEST_URI);
		
		requestDispatcher.handle(context, request, response);
		
		verify(customHandler, times(1)).handle(context, request, response);
		verifyZeroInteractions(defaultHandler);
	}
	
	@Test
	public void handlesRequestUsingDefaultHandlerIfNoAppropriateCustomHandler() throws Exception {
		HttpResponse response = createDummyResponse();
		HttpServerContext context = createDummyContext();
		HttpRequest request = createRequestWithUri("unknown.html");
		
		requestDispatcher.handle(context, request, response);
		verify(defaultHandler, times(1)).handle(context, request, response);
		verifyZeroInteractions(customHandler);
	}
	
	@Test
	public void throwsHttpServerExceptionForEveryRuntimeException() throws Exception {
		HttpResponse response = createDummyResponse();
		HttpServerContext context = createDummyContext();
		HttpRequest request = createRequestWithUri(REQUEST_URI);
		RuntimeException runtimeException = new RuntimeException("Some error occurred");
		doThrow(runtimeException).when(customHandler).handle(any(), any(), any());
		
		expected.expect(HttpServerException.class);
		expected.expectCause(sameInstance(runtimeException));
		expected.expectMessage(containsString("Handle request: " + request.getUri() + " failed: "));
		
		requestDispatcher.handle(context, request, response);
		
	}
	
	@Test
	public void rethrowsHttpServerExceptionIfOneOccurs() throws Exception {
		HttpResponse response = createDummyResponse();
		HttpServerContext context = createDummyContext();
		HttpRequest request = createRequestWithUri(REQUEST_URI);
		HttpServerException serverException = new HttpServerException("Some error occurred");
		doThrow(serverException).when(customHandler).handle(any(), any(), any());
		
		expected.expect(sameInstance(serverException));
		expected.expectMessage(containsString("Some error occurred"));
		
		requestDispatcher.handle(context, request, response);
		
	}

	private static HttpRequest createRequestWithUri(String requestUri) {
		return new DefaultHttpRequest("GET", requestUri, "HTTP/1.1", "localhost", Collections.emptyMap(),
				Collections.emptyMap());
	}

	private static HttpServerContext createDummyContext() {
		return mock(HttpServerContext.class);
	}

	private static HttpResponse createDummyResponse() {
		return new DefaultReadableHttpResponse();
	}

}

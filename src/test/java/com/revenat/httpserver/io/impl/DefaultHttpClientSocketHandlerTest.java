package com.revenat.httpserver.io.impl;

import static com.revenat.httpserver.io.impl.TestUtils.MIME_PROPS_RESOURCE;
import static com.revenat.httpserver.io.impl.TestUtils.SERVER_PROPS_RESOURCE;
import static com.revenat.httpserver.io.impl.TestUtils.STATUSES_PROPS_RESOURCE;
import static com.revenat.httpserver.io.impl.TestUtils.createMimeProperties;
import static com.revenat.httpserver.io.impl.TestUtils.createServerProperties;
import static com.revenat.httpserver.io.impl.TestUtils.createStatusesProperties;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;

import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.revenat.httpserver.io.Constants;
import com.revenat.httpserver.io.HttpHandlerRegistrar;
import com.revenat.httpserver.io.HttpResponse;
import com.revenat.httpserver.io.config.HttpClientSocketHandler;
import com.revenat.httpserver.io.config.HttpRequestDispatcher;
import com.revenat.httpserver.io.config.HttpResponseBuilder;
import com.revenat.httpserver.io.config.HttpServerConfig;
import com.revenat.httpserver.io.config.HttpServerResourceLoader;
import com.revenat.httpserver.io.config.ReadableHttpResponse;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultHttpClientSocketHandlerTest {
	private static final String RESPONSE_BODY = "test content";
	private static final String GET_REQUEST_CONTENT = "GET /index.html HTTP/1.1\r\n" + 
												 "Host: localhost\r\n" + 
												 "User-Agent: Mozilla/5.0\r\n" + 
												 "Accept: text/html\r\n" + 
												 "Connection: close\r\n\r\n";
	private static final String PUT_REQUEST_CONTENT = "PUT /index.html HTTP/1.1\r\n\r\n";
	private static final String GET_HTTP_NOT_SUPPORTED_REQUEST_CONTENT = "GET /index.html HTTP/1.0\r\n\r\n";
	private static final String CLIENT_REMOTE_ADDRESS = "localhost";
	
	private final InputStream clientInputStream = new ReaderInputStream(new StringReader(GET_REQUEST_CONTENT), "UTF-8");
	private final StringWriter responseContent = new StringWriter();
	private final OutputStream clientOuptuStream = new WriterOutputStream(responseContent, "UTF-8");
	
	private HttpServerConfig serverConfig;
	@Mock
	private Socket clientSocket;
	@Mock
	private HttpServerResourceLoader propLoader;
	@Mock
	private HttpRequestDispatcher requestDispatcher;
	@Mock
	private HttpResponseBuilder responseBuilder;
	
	private HttpClientSocketHandler handler;
	
	@Before
	public void setup() throws IOException {
		configurePropertiesLoader();
		when(responseBuilder.buildNewHttpResponse()).thenReturn(new DefaultReadableHttpResponse());
		serverConfig = new FakeHttpServerConfig(propLoader, requestDispatcher, responseBuilder);
		
		configureClientSocket();
	}

	private void configureClientSocket() throws IOException {
		when(clientSocket.getInputStream()).thenReturn(clientInputStream);
		when(clientSocket.getOutputStream()).thenReturn(clientOuptuStream);
		when(clientSocket.getRemoteSocketAddress()).thenReturn(new StubSocketAddress());
	}

	private void configurePropertiesLoader() {
		when(propLoader.loadProperties(STATUSES_PROPS_RESOURCE)).thenReturn(createStatusesProperties());
		when(propLoader.loadProperties(MIME_PROPS_RESOURCE)).thenReturn(createMimeProperties());
		when(propLoader.loadProperties(SERVER_PROPS_RESOURCE)).thenReturn(createServerProperties());
	}

	@Test(expected = NullPointerException.class)
	public void throwsNullPointerExceptionIfConstructedWithNullClientSocket() throws Exception {
		handler = new DefaultHttpClientSocketHandler(null, serverConfig);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsNullPointerExceptionIfConstructedWithNullHttpServerConfig() throws Exception {
		handler = new DefaultHttpClientSocketHandler(clientSocket, null);
	}
	
	@Test
	public void setsKeepAliveToFalseOnClientSocket() throws Exception {
		handler = new DefaultHttpClientSocketHandler(clientSocket, serverConfig);
		
		handler.run();
		
		verify(clientSocket, times(1)).setKeepAlive(false);
	}
	
	@Test
	public void handlesClientGetRequestBySettingResponseBody() throws Exception {
		doAnswer(invocation -> {
			
				HttpResponse response = invocation.getArgument(2);
				response.setBody(RESPONSE_BODY);
				return null;
			}
		)
		.when(requestDispatcher).handle(Mockito.any(), Mockito.any(), Mockito.any(HttpResponse.class));
		handler = new DefaultHttpClientSocketHandler(clientSocket, serverConfig);
		
		handler.run();
		
		assertThat(responseContent.toString(), containsString(RESPONSE_BODY));
	}
	
	@Test
	public void setsResponseStatus500IfServerErrorOccursDuringReqeustHandling() throws Exception {
		doThrow(new IOException("Some error occurred"))
		.when(requestDispatcher).handle(Mockito.any(), Mockito.any(), Mockito.any(HttpResponse.class));
		handler = new DefaultHttpClientSocketHandler(clientSocket, serverConfig);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				ReadableHttpResponse response = invocation.getArgument(0);
				assertThat(response.getStatus(), equalTo(500));
				return null;
			}
		}).when(responseBuilder).prepareHttpResponse(Mockito.any(), Mockito.anyBoolean());
		
		handler.run();
	}
	
	@Test
	public void setsResponseStatus505IfHttpVersionNotSupported() throws Exception {;
		when(clientSocket.getInputStream()).thenReturn(new ReaderInputStream(
				new StringReader(GET_HTTP_NOT_SUPPORTED_REQUEST_CONTENT), "UTF-8"));
		handler = new DefaultHttpClientSocketHandler(clientSocket, serverConfig);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				ReadableHttpResponse response = invocation.getArgument(0);
				assertThat(response.getStatus(), equalTo(505));
				return null;
			}
		}).when(responseBuilder).prepareHttpResponse(Mockito.any(), Mockito.anyBoolean());
		
		handler.run();
	}
	
	@Test
	public void setsAllowResponseHeaderIfRequestMethodNotAllowed() throws Exception {;
		when(clientSocket.getInputStream()).thenReturn(new ReaderInputStream(new StringReader(PUT_REQUEST_CONTENT), "UTF-8"));
		handler = new DefaultHttpClientSocketHandler(clientSocket, serverConfig);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				ReadableHttpResponse response = invocation.getArgument(0);
				Map<String, String> headers = response.getHeaders();
				assertThat(headers.get("Allow"), equalTo(StringUtils.join(Constants.ALLOWED_METHODS, ", ")));
				return null;
			}
		}).when(responseBuilder).prepareHttpResponse(Mockito.any(), Mockito.anyBoolean());
		
		handler.run();
	}
	
	@SuppressWarnings("serial")
	private static class StubSocketAddress extends SocketAddress {
		@Override
		public String toString() {
			return CLIENT_REMOTE_ADDRESS;
		}
		
	}
	
	private static class FakeHttpServerConfig extends DefaultHttpServerConfig {
		private HttpRequestDispatcher dispatcher;
		private HttpResponseBuilder responseBuilder;

		FakeHttpServerConfig(HttpServerResourceLoader propertiesLoader,
				HttpRequestDispatcher dispatcher, HttpResponseBuilder responseBuilder) {
			super(new HttpHandlerRegistrar(), null, propertiesLoader);
			this.dispatcher = dispatcher;
			this.responseBuilder = responseBuilder;
		}
		@Override
		public HttpRequestDispatcher getHttpRequestDispatcher() {
			return dispatcher;
		}
		@Override
		public HttpResponseBuilder getHttpResponseBuilder() {
			return responseBuilder;
		}
		
	}

}

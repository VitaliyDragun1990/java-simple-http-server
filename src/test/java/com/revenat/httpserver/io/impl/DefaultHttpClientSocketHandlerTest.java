package com.revenat.httpserver.io.impl;

import static com.revenat.httpserver.io.impl.TestUtils.MIME_PROPS_RESOURCE;
import static com.revenat.httpserver.io.impl.TestUtils.SERVER_PROPS_RESOURCE;
import static com.revenat.httpserver.io.impl.TestUtils.STATUSES_PROPS_RESOURCE;
import static com.revenat.httpserver.io.impl.TestUtils.createMimeProperties;
import static com.revenat.httpserver.io.impl.TestUtils.createServerProperties;
import static com.revenat.httpserver.io.impl.TestUtils.createStatusesProperties;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Properties;

import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.output.WriterOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.revenat.httpserver.io.HttpResponse;
import com.revenat.httpserver.io.config.HttpClientSocketHandler;
import com.revenat.httpserver.io.config.HttpRequestDispatcher;
import com.revenat.httpserver.io.config.HttpServerConfig;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultHttpClientSocketHandlerTest {
	private static final String RESPONSE_BODY = "test content";
	private static final String GET_REQUEST_CONTENT = "GET /index.html HTTP/1.1\r\n" + 
												 "Host: localhost\r\n" + 
												 "User-Agent: Mozilla/5.0\r\n" + 
												 "Accept: text/html\r\n" + 
												 "Connection: close\r\n\r\n";
	private static final String PUT_REQUEST_CONTENT = "POT /index.html HTTP/1.1\r\n\r\n";
	private static final String CLIENT_REMOTE_ADDRESS = "localhost";
	
	private final InputStream clientInputStream = new ReaderInputStream(new StringReader(GET_REQUEST_CONTENT), "UTF-8");
	private final StringWriter responseContent = new StringWriter();
	private final OutputStream clientOuptuStream = new WriterOutputStream(responseContent, "UTF-8");
	
	private HttpServerConfig serverConfig;
	@Mock
	private Socket clientSocket;
	@Mock
	private PropertiesLoader propLoader;
	@Mock
	private HttpRequestDispatcher requestDispatcher;
	
	private HttpClientSocketHandler handler;
	
	@Before
	public void setup() throws IOException {
		configurePropertiesLoader();
		serverConfig = new FakeHttpServerConfig(null, propLoader, requestDispatcher);
		
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
	public void handlesClientGetRequestReturningResponseWithBody() throws Exception {
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
	public void returnsResponseWithStatus500IfServerErrorOccursDuringReqeustHandling() throws Exception {
		doThrow(new IOException("Some error occurred"))
		.when(requestDispatcher).handle(Mockito.any(), Mockito.any(), Mockito.any(HttpResponse.class));
		handler = new DefaultHttpClientSocketHandler(clientSocket, serverConfig);
		
		handler.run();
		
		assertThat(responseContent.toString(), containsString("500 Internal Server Error"));
	}
	
	@Test
	public void returnsResponseWithStatus405IfRequestMethodNotAllowed() throws Exception {;
		when(clientSocket.getInputStream()).thenReturn(new ReaderInputStream(new StringReader(PUT_REQUEST_CONTENT), "UTF-8"));
		handler = new DefaultHttpClientSocketHandler(clientSocket, serverConfig);
		
		handler.run();
		
		assertThat(responseContent.toString(), containsString("405 Method Not Allowed"));
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

		FakeHttpServerConfig(Properties overrideServerProperties, PropertiesLoader propertiesLoader,
				HttpRequestDispatcher dispatcher) {
			super(overrideServerProperties, propertiesLoader);
			this.dispatcher = dispatcher;
		}
		@Override
		public HttpRequestDispatcher getHttpRequestDispatcher() {
			return dispatcher;
		}
	}

}

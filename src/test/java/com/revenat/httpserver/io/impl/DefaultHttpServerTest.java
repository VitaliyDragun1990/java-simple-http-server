package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.Socket;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.revenat.httpserver.io.ServerInfo;
import com.revenat.httpserver.io.config.HttpClientSocketHandler;
import com.revenat.httpserver.io.config.HttpServerConfig;
import com.revenat.httpserver.io.exception.HttpServerException;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultHttpServerTest {
	private static final ServerInfo SERVER_INFO = new ServerInfo("test-server", 8085, 1);
	private static final ThreadFactory STUB_THREAD_FACTORY = (task) -> new Thread(task);
	@SuppressWarnings("unused")
	private static final HttpClientSocketHandler STUB_SOCKET_HANDLER = () -> {};
	
	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Mock
	private HttpServerConfig serverConfig;
	@Mock
	private DefaultHttpClientSocketHandler socketHandler;
	
	private DefaultHttpServer httpServer;
	
	@Before
	public void setup() {
		when(serverConfig.getServerInfo()).thenReturn(SERVER_INFO);
		when(serverConfig.buildNewHttpClientSocketHandler(Mockito.any())).thenReturn(socketHandler);
//		when(serverConfig.buildNewHttpClientSocketHandler(Mockito.any())).thenReturn(STUB_SOCKET_HANDLER);
		when(serverConfig.getWorkerThreadFactory()).thenReturn(STUB_THREAD_FACTORY);
		httpServer = new DefaultHttpServer(serverConfig);
	}
	
	@After
	public void tearDown() {
		httpServer.stop();
	}
	
	@Test
	public void throwsExceptionIfCreatedWithNullHttpServerConfig() throws Exception {
		expected.expect(NullPointerException.class);
		expected.expectMessage(containsString("HttpServerConfig can not be null"));
		
		httpServer = new DefaultHttpServer(null);
	}
	
	@Test
//	@Ignore("HttpServer stop() command exit JVM")
	public void throwsExceptionIfStartedMoreThanOnce() throws Exception {
		expected.expect(HttpServerException.class);
		expected.expectMessage(containsString("Current HTTP server already started or stopped!"));
		
		httpServer.start();
		httpServer.start();
	}
	
	@Test
//	@Ignore("HttpServer stop() command exit JVM")
	public void throwsExceptionIfRestartAfterStop() throws Exception {
		expected.expect(HttpServerException.class);
		expected.expectMessage(containsString("Current HTTP server already started or stopped!"));
		
		httpServer.start();
		httpServer.stop();
		httpServer.start();
	}
	
	@SuppressWarnings({ "unused", "resource" })
	@Test
	public void handlesClientConnection() throws Exception {
		httpServer.start();
		Socket clientSocket = new Socket("localhost", 8085);
		
		TimeUnit.SECONDS.sleep(1);
		
		verify(serverConfig, times(1)).buildNewHttpClientSocketHandler(Mockito.any(Socket.class));
	}

}

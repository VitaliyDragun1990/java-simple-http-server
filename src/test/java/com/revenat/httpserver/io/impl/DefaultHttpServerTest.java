package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.ServerSocket;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.revenat.httpserver.io.ServerInfo;
import com.revenat.httpserver.io.config.HttpClientSocketHandler;
import com.revenat.httpserver.io.config.HttpServerConfig;
import com.revenat.httpserver.io.exception.HttpServerException;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultHttpServerTest {
	private static final ServerInfo SERVER_INFO = new ServerInfo("test-server", 8085, 1);
	private static final ThreadFactory STUB_THREAD_FACTORY = (task) -> new Thread(task);

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
		when(serverConfig.getWorkerThreadFactory()).thenReturn(STUB_THREAD_FACTORY);
	}

	private DefaultHttpServer createServer() {
		return new DefaultHttpServer(serverConfig);
	}

	@After
	public void tearDown() {
		if (httpServer != null) {

			httpServer.stop();
		}
	}

	@Test
	public void throwsExceptionIfCreatedWithNullHttpServerConfig() throws Exception {
		expected.expect(NullPointerException.class);
		expected.expectMessage(containsString("HttpServerConfig can not be null"));

		httpServer = new DefaultHttpServer(null);
	}

	@Test
	public void throwsExceptionIfStartedMoreThanOnce() throws Exception {
		expected.expect(HttpServerException.class);
		expected.expectMessage(containsString("Current HTTP server already started or stopped!"));
		httpServer = createServer();

		httpServer.start();
		httpServer.start();
	}

	@Test
	public void throwsExceptionIfRestartAfterStop() throws Exception {
		expected.expect(HttpServerException.class);
		expected.expectMessage(containsString("Current HTTP server already started or stopped!"));
		httpServer = createServer();

		httpServer.start();
		httpServer.stop();
		httpServer.start();
	}

	@SuppressWarnings({ "unused", "resource" })
	@Test
	public void handlesClientConnection() throws Exception {
		httpServer = createServer();
		httpServer.start();
		Socket clientSocket = new Socket("localhost", 8085);

		TimeUnit.SECONDS.sleep(1);

		verify(serverConfig, times(1)).buildNewHttpClientSocketHandler(Mockito.any(Socket.class));
	}

	@SuppressWarnings({ "unused", "resource" })
	@Test
	public void stopsServerIfErrorDuringProcessingClientSocket() throws Exception {
		when(serverConfig.buildNewHttpClientSocketHandler(Mockito.any()))
				.thenAnswer(new Answer<HttpClientSocketHandler>() {
					@Override
					public HttpClientSocketHandler answer(InvocationOnMock invocation) throws Throwable {
						throw new IOException("Error processing client socket");
					}
				});
		httpServer = createServer();
		httpServer.start();
		Socket clientSocket = new Socket("localhost", 8085);

		TimeUnit.SECONDS.sleep(1);

		assertThat(httpServer.isServerStopped(), is(true));
	}

	@Test(expected = HttpServerException.class)
	public void throwsExceptionIfServerPortAlreadInUse() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(8085)) {

			httpServer = createServer();
		}
	}
}

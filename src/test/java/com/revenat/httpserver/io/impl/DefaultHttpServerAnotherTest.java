package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.revenat.httpserver.io.ServerInfo;
import com.revenat.httpserver.io.config.HttpClientSocketHandler;
import com.revenat.httpserver.io.config.HttpServerConfig;

/**
 * This test class uses test approach of heavy mocking
 * and applying inheritance for testing SUT behavior.
 * 
 * Personally this approach seems discouraged for me.
 * 
 * @author Vitaly Dragun
 *
 */
/**
 * @author Vitaly Dragun
 *
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultHttpServerAnotherTest {
	
	private static final int THREAD_COUNT_LIMIT = 5;

	private static final int UNLIMITED_COUNT = 0;

	private DefaultHttpServer server;
	
	private HttpServerConfig httpServerConfig;
	private ServerSocket serverSocket;
	private ExecutorService executorService;
	private Thread mainServerThread;
	
	@Before
	public void setup() {
		httpServerConfig = mock(HttpServerConfig.class);
		serverSocket = mock(ServerSocket.class);
		executorService = mock(ExecutorService.class);
		mainServerThread = mock(Thread.class);
	}
	
	@Test
	public void createsMainServerThread() throws Exception {
		server = new DefaultHttpServer(httpServerConfig) {
			@Override
			protected ExecutorService createExecutorService() {
				return executorService;
			}
			@Override
			protected Runnable createServerRunnable() {
				return mock(Runnable.class);
			}
			@Override
			protected ServerSocket createServerSocket() {
				return serverSocket;
			}
		};
		
		Thread thread = server.createMainServerThread(mock(Runnable.class));
		
		assertThat(thread.getPriority(), equalTo(Thread.MAX_PRIORITY));
		assertThat(thread.getName(), equalTo("Main Server Thread"));
		assertThat(thread.isDaemon(), is(false));
		assertThat(thread.isAlive(), is(false));
	}
	
	@Test
	public void successfullyDispatchesClientConnection() throws Exception {
		final Runnable[] jobs = new Runnable[1];
		server = new DefaultHttpServer(httpServerConfig) {
			@Override
			protected ExecutorService createExecutorService() {
				return executorService;
			}
			@Override
			protected Thread createMainServerThread(Runnable job) {
				return mainServerThread;
			}
			@Override
			protected Runnable createServerRunnable() {
				Runnable job = super.createServerRunnable();
				jobs[0] = job;
				return job;
			}
			@Override
			protected ServerSocket createServerSocket() {
				return serverSocket;
			}
		};
		when(mainServerThread.isInterrupted()).thenReturn(false, true);
		Socket clientSocket = mock(Socket.class);
		when(serverSocket.accept()).thenReturn(clientSocket);
		HttpClientSocketHandler socketHandler = mock(HttpClientSocketHandler.class);
		when(httpServerConfig.buildNewHttpClientSocketHandler(clientSocket)).thenReturn(socketHandler);
		
		jobs[0].run();
		
		verify(mainServerThread, times(2)).isInterrupted();
		verify(serverSocket).accept();
		verify(httpServerConfig).buildNewHttpClientSocketHandler(clientSocket);
		verify(executorService).submit(socketHandler);
		
		verify(httpServerConfig, never()).close();
		verify(executorService, never()).shutdown();
	}
	
	@Test
	public void closesResourcesIfProcessingClientConnectionFailed() throws Exception {
		final Runnable[] jobs = new Runnable[1];
		server = new DefaultHttpServer(httpServerConfig) {
			@Override
			protected ExecutorService createExecutorService() {
				return executorService;
			}
			@Override
			protected Thread createMainServerThread(Runnable job) {
				return mainServerThread;
			}
			@Override
			protected Runnable createServerRunnable() {
				Runnable job = super.createServerRunnable();
				jobs[0] = job;
				return job;
			}
			@Override
			protected ServerSocket createServerSocket() {
				return serverSocket;
			}
		};
		when(mainServerThread.isInterrupted()).thenReturn(false, true);
		when(serverSocket.accept()).thenThrow(new IOException("Accept failed"));
		
		jobs[0].run();
		
		verify(mainServerThread, times(1)).isInterrupted();
		verify(serverSocket).accept();
		verify(executorService, never()).submit(Mockito.any(Runnable.class));
		
		verify(httpServerConfig).close();
		verify(executorService).shutdown();
	}
	
	@Test
	public void createsCachedExecutorServiceForUnlimitedThreadCount() throws Exception {
		ThreadFactory threadFactory = mock(ThreadFactory.class);
		when(httpServerConfig.getWorkerThreadFactory()).thenReturn(threadFactory);
		ServerInfo serverInfo = mock(ServerInfo.class);
		when(httpServerConfig.getServerInfo()).thenReturn(serverInfo);
		when(serverInfo.getThreadCount()).thenReturn(UNLIMITED_COUNT);
		
		server = new DefaultHttpServer(httpServerConfig) {
			@Override
			protected Thread createMainServerThread(Runnable job) {
				return mainServerThread;
			}
			@Override
			protected Runnable createServerRunnable() {
				return mock(Runnable.class);
			}
			@Override
			protected ServerSocket createServerSocket() {
				return serverSocket;
			}
		};
		
		ThreadPoolExecutor executorService = (ThreadPoolExecutor) server.createExecutorService();
		assertThat(executorService.getCorePoolSize(), equalTo(0));
		assertThat(executorService.getMaximumPoolSize(), equalTo(Integer.MAX_VALUE));
		assertThat(executorService.getKeepAliveTime(TimeUnit.SECONDS), equalTo(60L));
		assertThat(executorService.getThreadFactory(), sameInstance(threadFactory));
		assertThat(executorService.getQueue(), instanceOf(SynchronousQueue.class));
	}
	
	@Test
	public void createsFixedExecutorServiceForLimitedThreadCount() throws Exception {
		ThreadFactory threadFactory = mock(ThreadFactory.class);
		when(httpServerConfig.getWorkerThreadFactory()).thenReturn(threadFactory);
		ServerInfo serverInfo = mock(ServerInfo.class);
		when(httpServerConfig.getServerInfo()).thenReturn(serverInfo);
		when(serverInfo.getThreadCount()).thenReturn(THREAD_COUNT_LIMIT);
		
		server = new DefaultHttpServer(httpServerConfig) {
			@Override
			protected Thread createMainServerThread(Runnable job) {
				return mainServerThread;
			}
			@Override
			protected Runnable createServerRunnable() {
				return mock(Runnable.class);
			}
			@Override
			protected ServerSocket createServerSocket() {
				return serverSocket;
			}
		};
		
		ThreadPoolExecutor executorService = (ThreadPoolExecutor) server.createExecutorService();
		assertThat(executorService.getCorePoolSize(), equalTo(THREAD_COUNT_LIMIT));
		assertThat(executorService.getMaximumPoolSize(), equalTo(THREAD_COUNT_LIMIT));
		assertThat(executorService.getKeepAliveTime(TimeUnit.SECONDS), equalTo(0L));
		assertThat(executorService.getThreadFactory(), sameInstance(threadFactory));
		assertThat(executorService.getQueue(), instanceOf(LinkedBlockingQueue.class));
	}
}













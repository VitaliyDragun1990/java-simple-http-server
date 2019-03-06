package com.revenat.httpserver.io.impl;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revenat.httpserver.io.HttpServer;
import com.revenat.httpserver.io.config.HttpServerConfig;
import com.revenat.httpserver.io.exception.HttpServerException;

/**
 * Reference implementation of the {@link HttpServer}
 * 
 * @author Vitaly Dragun
 *
 */
class DefaultHttpServer implements HttpServer {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHttpServer.class);
	private final HttpServerConfig httpServerConfig;
	private final ServerSocket serverSocket;
	private final ExecutorService executorService;
	private final Thread mainServerThread;
	private volatile boolean serverStopped;
	
	DefaultHttpServer(HttpServerConfig httpServerConfig) {
		this.httpServerConfig = requireNonNull(httpServerConfig, "HttpServerConfig can not be null");
		this.executorService = createExecutorService();
		this.mainServerThread = createMainServerThread(createServerRunnable());
		this.serverSocket = createServerSocket();
		this.serverStopped = false;
	}

	/**
	 * Creates {@link ExecutorService} responsible for managing server's worker threads.
	 */
	protected ExecutorService createExecutorService() {
		ThreadFactory threadFactory = httpServerConfig.getWorkerThreadFactory();
		int threadCount = httpServerConfig.getServerInfo().getThreadCount();
		if (threadCount > 0) {
			return Executors.newFixedThreadPool(threadCount, threadFactory);
		} else {
			return Executors.newCachedThreadPool(threadFactory);
		}
	}
	
	/**
	 * Creates main server thread
	 * @param job {@link Runnable} with instructions what server should do
	 */
	protected Thread createMainServerThread(Runnable job) {
		Thread serverThread = new Thread(job,  "Main Server Thread");
		serverThread.setPriority(Thread.MAX_PRIORITY);
		serverThread.setDaemon(false);
		return serverThread;
	}
	
	/**
	 * Creates runnable that encapsulate HTTP server's main job - accepting clients sockets
	 * and passing them to handlers to process clients requests.
	 */
	protected Runnable createServerRunnable() {
		return () -> {
			while (!mainServerThread.isInterrupted()) {
				try {
					Socket cleintSocket = serverSocket.accept();
					executorService.submit(httpServerConfig.buildNewHttpClientSocketHandler(cleintSocket));
				} catch (IOException e) {
					if (!serverSocket.isClosed()) {
						LOGGER.error("Can not accept client socket: " + e.getMessage(), e);
					}
					destroyHttpServer();
					break;
				}
			}
//			System.exit(0);
		};
	}
	
	/**
	 * Creates HTTP server socket
	 */
	protected ServerSocket createServerSocket() {
		int serverPort = httpServerConfig.getServerInfo().getPort();
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(serverPort);
			socket.setReuseAddress(true);
			return socket;
		} catch (IOException e) {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException ex) {/*ignore*/}
			}
			throw new HttpServerException("Can not create server socket with port=" + serverPort, e);
		}
	}

	@Override
	public void start() {
		if (mainServerThread.getState() != Thread.State.NEW) {
			throw new HttpServerException("Current HTTP server already started or stopped!"
					+ " Please create a new HTTP server instance.");
		}
		Runtime.getRuntime().addShutdownHook(getShutdownHook());
		mainServerThread.start();
		LOGGER.info("HTTP server started: {}", httpServerConfig.getServerInfo());

	}

	@Override
	public void stop() {
		LOGGER.info("Detect stop cmd");
		mainServerThread.interrupt();
		try {
			serverSocket.close();
		} catch (IOException e) {
			LOGGER.warn("Error during closing HTTP server socket: " + e.getMessage(), e);
		}

	}
	
	/**
	 * Creates thread responsible for gracefully closing HTTP server
	 * on JVM exit event.
	 */
	protected Thread getShutdownHook() {
		return new Thread(() -> {
			if (!serverStopped) {
				destroyHttpServer();
			}
		}, "ShutdownHook");
	}

	/**
	 * Gracefully closes HTTP server resources (e.g. HttpServerConfig, ExecutorService)
	 * and sets {@code serverStopped} flag to {@code true}
	 */
	protected void destroyHttpServer() {
		try {
			httpServerConfig.close();
		} catch (Exception e) {
			LOGGER.error("Close httpServerConfig failed: " + e.getMessage(), e);
		}
		executorService.shutdown();
		LOGGER.info("HTTP Server stopped");
		serverStopped = true;
	}
}

package com.revenat.httpserver.io;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revenat.httpserver.io.handler.HelloWorldHttpHandler;
import com.revenat.httpserver.io.handler.ServerInfoHttpHandler;
import com.revenat.httpserver.io.handler.TestJDBCHandler;
import com.revenat.httpserver.io.impl.HttpServerFactory;

/**
 * Component that represents command line interface to start and stop {@link HttpServer}
 * @author Vitaly Dragun
 *
 */
public class CLI {
	private static final Logger LOGGER = LoggerFactory.getLogger(CLI.class);
	private static final List<String> QUIT_CMDS = Collections.unmodifiableList(Arrays.asList("q", "quit", "exit"));

	public static void main(String[] args) {
		Thread.currentThread().setName("CLI-main thread");
		
		try {
			HttpServerFactory httpServerFactory = HttpServerFactory.create();
			HttpServer httpServer = httpServerFactory.createHttpServer(getHandlerRegistrar(), null);
			httpServer.start();
			
			waitForStopCommand(httpServer);
		} catch(Exception e) {
			LOGGER.error("Can't execute cmd: " + e.getMessage(), e);
		}
	}
	
	private static HttpHandlerRegistrar getHandlerRegistrar() {
		return new HttpHandlerRegistrar()
				.registerHandler("/info", new ServerInfoHttpHandler())
				.registerHandler("/jdbc", new TestJDBCHandler())
				.registerHandler("/hello", new HelloWorldHttpHandler())
				;
	}

	private static void waitForStopCommand(HttpServer httpServer) {
		try(Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name())) {
			while (true) {
				String cmd = scanner.nextLine();
				if (QUIT_CMDS.contains(cmd.toLowerCase())) {
					httpServer.stop();
					break;
				} else {
					LOGGER.warn("Unsupported cmd: {}. To shutdown server please type: q", cmd);
				}
			}
		} finally {
			System.exit(0);
		}
	}

}

package com.revenat.httpserver.io;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ServerInfoTest {

	private static final int THREAD_COUNT = 1;
	private static final int SERVER_PORT = 9090;
	private static final String SERVER_NAME = "test-server";
	
	private ServerInfo serverInfo;
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfCreatedWithNullName() throws Exception {
		serverInfo = new ServerInfo(null, 0, 0);
	}
	
	@Test
	public void holdsServerNameValue() throws Exception {
		serverInfo = new ServerInfo(SERVER_NAME, SERVER_PORT, THREAD_COUNT);
		
		assertThat(serverInfo.getName(), equalTo(SERVER_NAME));
	}
	
	@Test
	public void holdsServerPortValue() throws Exception {
		serverInfo = new ServerInfo(SERVER_NAME, SERVER_PORT, THREAD_COUNT);
		
		assertThat(serverInfo.getPort(), equalTo(SERVER_PORT));
	}
	
	@Test
	public void holdsThreadCountValue() throws Exception {
		serverInfo = new ServerInfo(SERVER_NAME, SERVER_PORT, THREAD_COUNT);
		
		assertThat(serverInfo.getThreadCount(), equalTo(THREAD_COUNT));
	}
	
	@Test
	public void returnsStringRepresentationOfHttpServerInfo() throws Exception {
		serverInfo = new ServerInfo(SERVER_NAME, SERVER_PORT, THREAD_COUNT);
		String actual = serverInfo.toString();
		String expected = String.format("ServerInfo [name=%s, port=%s, threadCount=%s]",
				SERVER_NAME, SERVER_PORT, THREAD_COUNT);
		
		assertThat(actual, equalTo(expected));
	}

}

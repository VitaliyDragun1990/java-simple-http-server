package com.revenat.httpserver.io.exception;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class HttpVersionNotSupportedExceptionTest {
	
	private static final String ERROR_MESSAGE = "Some error occurred";
	private static final String STARTING_LINE = "GET / HTTP/1.0";
	private HttpVersionNotSupportedException exception;

	@Test
	public void canBeCreatedWithMessageAndStartingLine() throws Exception {
		exception = new HttpVersionNotSupportedException(ERROR_MESSAGE, STARTING_LINE);
		
		assertThat(exception, not(nullValue()));
	}
	
	@Test
	public void holdsSpecifiedStartingLine() throws Exception {
		exception = new HttpVersionNotSupportedException(ERROR_MESSAGE, STARTING_LINE);
		
		assertThat(exception.getStartingLine(), equalTo(STARTING_LINE));
	}
	
	@Test
	public void holdsStatusCodeEqualTo505ByDefault() throws Exception {
		exception = new HttpVersionNotSupportedException(ERROR_MESSAGE, STARTING_LINE);
		
		assertThat(exception.getStatusCode(), equalTo(505));
	}

}

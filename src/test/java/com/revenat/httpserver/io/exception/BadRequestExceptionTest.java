package com.revenat.httpserver.io.exception;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class BadRequestExceptionTest {
	
	private static final String ERROR_MESSAGE = "Some error occurred";
	private static final String STARTING_LINE = "GET / HTTP/1.0";
	private static final RuntimeException CAUSE = new RuntimeException("Something went wrong");

	private BadRequestException exception;
	
	@Test
	public void canBeCreatedWithMessageCauseAndStartingLine() throws Exception {
		exception = new BadRequestException(ERROR_MESSAGE, CAUSE, STARTING_LINE);
		
		assertThat(exception, not(nullValue()));
	}
	
	@Test
	public void holdsStatusCodeEqualTo400ByDefault() throws Exception {
		exception = new BadRequestException(ERROR_MESSAGE, CAUSE, STARTING_LINE);
		
		assertThat(exception.getStatusCode(), equalTo(400));
	}

}

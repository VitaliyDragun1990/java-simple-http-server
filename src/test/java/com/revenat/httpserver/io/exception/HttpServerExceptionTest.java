package com.revenat.httpserver.io.exception;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class HttpServerExceptionTest {
	
	private static final int DEFAULT_SATUS_CODE = 500;
	private static final String ERROR_MESSAGE = "Some error occurred";
	private static final RuntimeException CAUSE = new RuntimeException();
	
	private HttpServerException exception;

	@Test
	public void canBeCreatedWithMessageOnly() throws Exception {
		exception = new HttpServerException(ERROR_MESSAGE);
		
		assertThat(exception, not(nullValue()));
	}
	
	@Test
	public void canBecreatedWithMessageAndCause() throws Exception {
		exception = new HttpServerException(ERROR_MESSAGE, CAUSE);
		
		assertThat(exception, not(nullValue()));
	}
	
	@Test
	public void canBecreatedWithCauseOnly() throws Exception {
		exception = new HttpServerException(CAUSE);
		
		assertThat(exception, not(nullValue()));
	}
	
	@Test
	public void holdsDefaultStatusCodeOf500() throws Exception {
		exception = new HttpServerException(ERROR_MESSAGE);
		
		assertThat(exception.getStatusCode(), equalTo(DEFAULT_SATUS_CODE));
	}
	
	@Test
	public void canChangeDefaultStatusCode() throws Exception {
		exception = new HttpServerException(ERROR_MESSAGE);
		
		int newStatusCode = 400;
		exception.setStatusCode(newStatusCode);
	
		assertThat(exception.getStatusCode(), equalTo(newStatusCode));
	}

}

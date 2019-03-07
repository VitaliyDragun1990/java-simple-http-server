package com.revenat.httpserver.io.exception;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

import org.junit.Test;

public class HttpServerConfigExceptionTest {
	private static final String ERROR_MESSAGE = "Some error occurred";
	private static final RuntimeException CAUSE = new RuntimeException();
	
	private HttpServerConfigException exception;

	@Test
	public void canBeCreatedWithMessageOnly() throws Exception {
		exception = new HttpServerConfigException(ERROR_MESSAGE);
		
		assertThat(exception, not(nullValue()));
	}
	
	@Test
	public void canBecreatedWithMessageAndCause() throws Exception {
		exception = new HttpServerConfigException(ERROR_MESSAGE, CAUSE);
		
		assertThat(exception, not(nullValue()));
	}
	
	@Test
	public void canBecreatedWithCauseOnly() throws Exception {
		exception = new HttpServerConfigException(CAUSE);
		
		assertThat(exception, not(nullValue()));
	}

}

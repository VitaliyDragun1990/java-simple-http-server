package com.revenat.httpserver.io.exception;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.revenat.httpserver.io.Constants;

public class MethodNotAllowedExceptionTest {
	private static final String METHOD = "PUT";
	private static final String STARTING_LINE = "PUT / HTTP/1.1";
	
	private MethodNotAllowedException exception;

	@Test
	public void canBeCreatedWithMethodAndStartingLine() throws Exception {
		exception = new MethodNotAllowedException(METHOD, STARTING_LINE);
		
		assertThat(exception, not(nullValue()));
	}
	
	@Test
	public void holdsSpecifiedStartingLine() throws Exception {
		exception = new MethodNotAllowedException(METHOD, STARTING_LINE);
		
		assertThat(exception.getStartingLine(), equalTo(STARTING_LINE));
	}
	
	@Test
	public void holdsStatusCodeEqualTo405ByDefault() throws Exception {
		exception = new MethodNotAllowedException(METHOD, STARTING_LINE);
		
		assertThat(exception.getStatusCode(), equalTo(405));
	}
	
	@Test
	public void containsErrorMessageWithAllowedMethods() throws Exception {
		exception = new MethodNotAllowedException(METHOD, STARTING_LINE);
		
		assertThat(exception.getMessage(), containsString(Constants.ALLOWED_METHODS.toString()));
	}

}

package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.input.ReaderInputStream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.revenat.httpserver.io.config.ReadableHttpResponse;
import com.revenat.httpserver.io.exception.HttpServerException;

public class DefaultReadableHttpResponseTest {

	private ReadableHttpResponse response;
	
	@Before
	public void setup() {
		response = new DefaultReadableHttpResponse();
	}

	@Test
	public void containsEmptyBodyWhenCreated() throws Exception {
		response = new DefaultReadableHttpResponse();

		assertThat(response.getBodyLength(), equalTo(0));
		assertThat(response.isBodyEmpty(), is(true));

	}

	@Test
	public void containsNoHeadersWhenCreated() throws Exception {
		response = new DefaultReadableHttpResponse();

		Map<String, String> headers = response.getHeaders();

		assertThat(headers.size(), equalTo(0));
	}

	@Test
	public void containsZeroStatusWhenCreated() throws Exception {
		response = new DefaultReadableHttpResponse();

		assertThat(response.getStatus(), equalTo(0));
	}
	
	@Test
	public void containsAddedHeaders() throws Exception {
		response.setHeader("name", "value");
		response.setHeader("name2", "value2");
		
		Map<String, String> headers = response.getHeaders();
		
		assertThat(headers.size(), equalTo(2));
		assertThat(headers, hasEntry("name", "value"));
		assertThat(headers, hasEntry("name2", "value2"));
		
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfHeaderNameIsNull() throws Exception {
		response.setHeader(null, "value");
		
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfHeaderValueIsNull() throws Exception {
		response.setHeader("name", null);
		
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfPassNullStringToBody() throws Exception {
		response.setBody((String)null);
		
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfPassNullInputStreamToBody() throws Exception {
		response.setBody((InputStream)null);
		
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfPassNullReaderToBody() throws Exception {
		response.setBody((Reader)null);
		
	}
	
	@Test
	public void setsBodyFromString() throws Exception {
		String bodyContent = "Body content";
		
		response.setBody(bodyContent);
		
		byte[] body = response.getBody();
		assertThat(new String(body, StandardCharsets.UTF_8), equalTo(bodyContent));
		
	}
	
	@Test
	public void setsBodyContentFromInputStream() throws Exception {
		String bodyContent = "Body content";
		StringReader reader = new StringReader(bodyContent);
		
		
		response.setBody(new ReaderInputStream(reader, StandardCharsets.UTF_8));
		
		byte[] body = response.getBody();
		assertThat(new String(body, StandardCharsets.UTF_8), equalTo(bodyContent));
	}
	
	@Test
	public void setsBodyContentFromReader() throws Exception {
		String bodyContent = "Body content";
		StringReader reader = new StringReader(bodyContent);
		
		
		response.setBody(reader);
		
		byte[] body = response.getBody();
		assertThat(new String(body, StandardCharsets.UTF_8), equalTo(bodyContent));
	}
	
	@Test(expected = HttpServerException.class)
	public void throwsExceptionIfErrorOccursDuringReadingBodyFromInputStream() throws Exception {
		InputStream in = mock(InputStream.class);
		when(in.read()).thenThrow(new IOException());
		
		response.setBody(in);
		
	}
	
	@SuppressWarnings("rawtypes")
	@Test(expected = HttpServerException.class)
	public void throwsExceptionIfErrorOccursDuringReadingBodyFromReader() throws Exception {
		Reader reader = mock(Reader.class, new Answer() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				throw new IOException("Something went wrong");
			}
		});
		
		response.setBody(reader);
		
	}
	

}

package com.revenat.httpserver.io.impl.defaults;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.revenat.httpserver.io.config.HttpResponseBuilder;
import com.revenat.httpserver.io.config.ReadableHttpResponse;
import com.revenat.httpserver.io.impl.defaults.DateTimeProvider;
import com.revenat.httpserver.io.impl.defaults.DefaultHttpResponseBuilder;
import com.revenat.httpserver.io.impl.defaults.DefaultReadableHttpResponse;

@RunWith(MockitoJUnitRunner.class)
public class DefaultHttpResponseBuilderTest {
	private static final String DATE_TIME_STRING = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now());

	@Mock
	private DateTimeProvider provider;

	private HttpResponseBuilder builder;

	@Before
	public void setup() {
		builder = new DefaultHttpResponseBuilder(provider);
		when(provider.getDateTimeString()).thenReturn(DATE_TIME_STRING);
	}

	@Test
	public void buildsNewPartiallyFilledResponse() throws Exception {
		ReadableHttpResponse response = builder.buildNewHttpResponse();

		assertNotNull("Newly created response can not be null", response);

	}

	@Test
	public void buildsNewResponseWithStarterHeaders() throws Exception {
		ReadableHttpResponse response = builder.buildNewHttpResponse();

		Map<String, String> headers = response.getHeaders();

		assertThat(headers.size(), equalTo(5));
	}

	@Test
	public void setsStarterHeadersForNewlyCreatedResponse() throws Exception {
		ReadableHttpResponse response = builder.buildNewHttpResponse();

		Map<String, String> headers = response.getHeaders();

		assertThat(headers.get("Server"), equalTo("Devstudy HTTP server"));
		assertThat(headers.get("Content-Language"), equalTo("en"));
		assertThat(headers.get("Connection"), equalTo("close"));
		assertThat(headers.get("Content-Type"), equalTo("text/html"));
		assertThat(headers.get("Date"), equalTo(DATE_TIME_STRING));
	}
	
	@Test
	public void setsContentLengthHeaderToZeroIfProvidedResponseHasNoBody() throws Exception {
		ReadableHttpResponse response = new DefaultReadableHttpResponse();
		
		builder.prepareHttpResponse(response, false);
		
		Map<String, String> headers = response.getHeaders();
		assertThat(headers.get("Content-Length"), equalTo("0"));
	}
	
	@Test
	public void setsContentLengthHeaderEqualToBodyLengthOfTheProvidedResponse() throws Exception {
		ReadableHttpResponse response = new DefaultReadableHttpResponse();
		String bodyContent = "Body content";
		response.setBody(bodyContent);
		
		builder.prepareHttpResponse(response, false);
		
		String expectedContentLength = String.valueOf(bodyContent.getBytes().length);
		Map<String, String> headers = response.getHeaders();
		assertThat(headers.get("Content-Length"), equalTo(expectedContentLength));
	}
	
	@Test
	public void clearesBodyOfTheProvidedResponseIfFlagSetToTrue() throws Exception {
		ReadableHttpResponse response = new DefaultReadableHttpResponse();
		String bodyContent = "Body content";
		response.setBody(bodyContent);
		boolean isClearBody = true;
		
		builder.prepareHttpResponse(response, isClearBody);
		
		assertThat(response.getBodyLength(), equalTo(0));
		
	}

}

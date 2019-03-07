package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.revenat.httpserver.io.HtmlTemplateManager;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.ServerInfo;
import com.revenat.httpserver.io.config.HttpResponseBuilder;
import com.revenat.httpserver.io.config.HttpServerConfig;
import com.revenat.httpserver.io.config.ReadableHttpResponse;

@RunWith(MockitoJUnitRunner.class)
public class DefaultHttpResponseBuilderTest {
	private static final ZonedDateTime CURRENT_DATE_TIME = ZonedDateTime.now();
	private static final String DATE_TIME_STRING = DateTimeFormatter.RFC_1123_DATE_TIME.format(CURRENT_DATE_TIME);
	private static final int BAD_REQUEST_STATUS = 400;
	private static final String BAD_REQUEST_MESSAGE = "Bad Request";

	@Mock
	private DateTimeProvider provider;
	@Mock
	private HttpServerConfig config;
	@Mock
	private HttpServerContext context;
	@Mock
	private ServerInfo serverInfo;
	@Mock
	private HtmlTemplateManager templateManager;

	private HttpResponseBuilder builder;

	@Before
	public void setup() {
		builder = new DefaultHttpResponseBuilder(config, provider);
		when(provider.getCurrentDateTime()).thenReturn(CURRENT_DATE_TIME);
		when(config.getServerInfo()).thenReturn(serverInfo);
		when(config.getHttpServerContext()).thenReturn(context);
		when(context.getHtmlTemplateManager()).thenReturn(templateManager);
		when(serverInfo.getName()).thenReturn("HTTP server");
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

		assertThat(headers.get("Server"), equalTo("HTTP server"));
		assertThat(headers.get("Content-Language"), equalTo("en"));
		assertThat(headers.get("Connection"), equalTo("close"));
		assertThat(headers.get("Content-Type"), equalTo("text/html"));
		assertThat(headers.get("Date"), equalTo(DATE_TIME_STRING));
		verify(provider, times(1)).getCurrentDateTime();
		verify(serverInfo, times(1)).getName();
	}
	
	@Test
	public void setsContentLengthHeaderToZeroIfProvidedResponseHasNoBody() throws Exception {
		ReadableHttpResponse response = createEmptyResponse();
		
		builder.prepareHttpResponse(response, false);
		
		Map<String, String> headers = response.getHeaders();
		assertThat(headers.get("Content-Length"), equalTo("0"));
	}


	@Test
	public void setsContentLengthHeaderEqualToBodyLengthOfTheProvidedResponse() throws Exception {
		ReadableHttpResponse response = createEmptyResponse();
		String bodyContent = "Body content";
		response.setBody(bodyContent);
		
		builder.prepareHttpResponse(response, false);
		
		String expectedContentLength = String.valueOf(bodyContent.getBytes().length);
		Map<String, String> headers = response.getHeaders();
		assertThat(headers.get("Content-Length"), equalTo(expectedContentLength));
	}
	
	@Test
	public void clearesBodyOfTheProvidedResponseIfFlagSetToTrue() throws Exception {
		ReadableHttpResponse response = createEmptyResponse();
		String bodyContent = "Body content";
		response.setBody(bodyContent);
		boolean isClearBody = true;
		
		builder.prepareHttpResponse(response, isClearBody);
		
		assertThat(response.getBodyLength(), equalTo(0));
	}
	
	@Test
	public void setsErrorPageAsBodyIfResponseStatusIs400() throws Exception {
		final String errorPage = "error page";
		ReadableHttpResponse response = createEmptyResponse();
		response.setStatus(BAD_REQUEST_STATUS);
		when(config.getStatusMessage(BAD_REQUEST_STATUS)).thenReturn(BAD_REQUEST_MESSAGE);
		when(context.getHtmlTemplateManager()).thenReturn(templateManager);
		when(templateManager.processTemplate(Mockito.anyString(), Mockito.any())).then(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Map<String, Object> args = invocation.getArgument(1);
				assertThat(args.get("STATUS-CODE"), equalTo(BAD_REQUEST_STATUS));
				assertThat(args.get("STATUS-MESSAGE"), equalTo(BAD_REQUEST_MESSAGE));
				return errorPage;
			}
		});
		
		builder.prepareHttpResponse(response, false);
		
		String actualBody = new String(response.getBody(), StandardCharsets.UTF_8);
		assertThat(actualBody, equalTo(errorPage));
		
	}
	
	
	private static ReadableHttpResponse createEmptyResponse() {
		return new DefaultReadableHttpResponse();
	}

}

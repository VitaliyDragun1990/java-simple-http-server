package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
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
import com.revenat.httpserver.io.HttpHandler;
import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.config.ReadableHttpResponse;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultHttpHandlerTest {
	private static final Path ROOT_DIR = Paths.get(new File("src/test/resources/test_root").getAbsoluteFile().toURI());
	private static final String ROOT_DIR_CONTENT =      "<a href='/favicon.ico'>favicon.ico</a><br />\r\n"
												 +		"<a href='/index.html'>index.html</a><br />\r\n"
												 +		"<a href='/static'>static/</a><br />\r\n";

	@Mock
	private HttpServerContext serverContext;
	@Mock
	private HtmlTemplateManager templateManager;

	private HttpHandler handler;

	@Before
	public void setup() {
		when(serverContext.getRootPath()).thenReturn(ROOT_DIR);
		handler = new DefaultHttpHandler();
	}

	@Test
	public void setsResponseStatus404IfStaticResourceNotFound() throws Exception {
		HttpRequest request = createGetRequestFor("not_found.txt");
		ReadableHttpResponse response = createDefaultResponse();

		handler.handle(serverContext, request, response);

		assertThat(response.getStatus(), equalTo(404));

	}

	@Test
	public void setsHeaderContentTypeIfRequeiredResourceIsFile() throws Exception {
		when(serverContext.getContentType("html")).thenReturn("text/html");
		HttpRequest request = createGetRequestFor("index.html");
		ReadableHttpResponse response = createDefaultResponse();
		
		handler.handle(serverContext, request, response);
		
		assertThat(response.getHeaders().get("Content-Type"), equalTo("text/html"));
		verify(serverContext, times(1)).getContentType(anyString());
	}
	
	@Test
	public void setsLastModifiedHeaderIfRequiredResourceIsFile() throws Exception {
		when(serverContext.getContentType("html")).thenReturn("text/html");
		HttpRequest request = createGetRequestFor("index.html");
		ReadableHttpResponse response = createDefaultResponse();
		String lastModifiedExpected = getLastModifiedFor("index.html");
		
		handler.handle(serverContext, request, response);
		
		assertThat(response.getHeaders().get("Last-Modified"), equalTo(lastModifiedExpected));
	}
	
	@Test
	public void setsExpiredHeaderIfRequiredResourceIsCached() throws Exception {
		int expiresDays = 7;
		when(serverContext.getContentType("html")).thenReturn("text/html");
		when(serverContext.getExpiresDaysForResource("html")).thenReturn(expiresDays);
		HttpRequest request = createGetRequestFor("index.html");
		ReadableHttpResponse response = createDefaultResponse();
		
		handler.handle(serverContext, request, response);
		
		String expectedExpires = DateTimeFormatter.RFC_1123_DATE_TIME.format(
				ZonedDateTime.now().plus(expiresDays, ChronoUnit.DAYS));
		assertThat(response.getHeaders().get("Expires"), equalTo(expectedExpires));
		verify(serverContext, times(1)).getExpiresDaysForResource(anyString());
	}
	
	@Test
	public void doesNotSetExpiredHeaderIfRequiredResourceIsNotCached() throws Exception {
		when(serverContext.getContentType("js")).thenReturn("application/javascript");
		when(serverContext.getExpiresDaysForResource("js")).thenReturn(null);
		HttpRequest request = createGetRequestFor("static/js/jquery.js");
		ReadableHttpResponse response = createDefaultResponse();
		
		handler.handle(serverContext, request, response);
		
		assertThat(response.getHeaders().get("Expires"), nullValue());
		verify(serverContext, times(1)).getExpiresDaysForResource(anyString());
	}
	
	@Test
	public void writesResourceContentToResponseBody() throws Exception {
		when(serverContext.getContentType("js")).thenReturn("application/javascript");
		HttpRequest request = createGetRequestFor("static/js/jquery.js");
		ReadableHttpResponse response = createDefaultResponse();
		byte[] expectedResponseBodyContent = getResourceContent("static/js/jquery.js");

		handler.handle(serverContext, request, response);
		
		assertThat(response.getBody(), equalTo(expectedResponseBodyContent));
	}

	@Test
	public void listsDirectoryContentInTheResponseBodyIfRequestedResourceIsDirectory() throws Exception {
		HttpRequest request = createGetRequestFor("/");
		ReadableHttpResponse response = createDefaultResponse();
		when(serverContext.getHtmlTemplateManager()).thenReturn(templateManager);
		when(templateManager.pocessTemplate(Mockito.anyString(), Mockito.any())).then(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Map<String, Object> args = invocation.getArgument(1);
				return args.get("BODY").toString();
			}
		});
		
		handler.handle(serverContext, request, response);
		
		String bodyContent = new String(response.getBody(), "UTF-8");
		assertThat(bodyContent, containsString(ROOT_DIR_CONTENT));
	}
	

	private static ReadableHttpResponse createDefaultResponse() {
		ReadableHttpResponse response = new DefaultReadableHttpResponse();
		response.setStatus(200);
		return response;
	}

	private static HttpRequest createGetRequestFor(String resource) {
		HttpRequest request = new DefaultHttpRequest("GET", resource, "HTTP/1.1", "localhost", Collections.emptyMap(),
				Collections.emptyMap());
		return request;
	}
	
	private static String getLastModifiedFor(String resource) throws IOException {
		FileTime lastModifiedTime = Files.getLastModifiedTime(Paths.get(ROOT_DIR.toString(), resource));
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(((FileTime) lastModifiedTime).toInstant(), ZoneId.systemDefault());
		return DateTimeFormatter.RFC_1123_DATE_TIME.format(dateTime);
	}
	
	private static byte[] getResourceContent(String resource) throws IOException {
		Path resourcePath = Paths.get(ROOT_DIR.toString(), resource);
		return Files.readAllBytes(resourcePath);
	}

}

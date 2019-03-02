package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.output.WriterOutputStream;
import org.junit.Before;
import org.junit.Test;

import com.revenat.httpserver.io.config.HttpResponseWriter;
import com.revenat.httpserver.io.config.ReadableHttpResponse;

public class DefaultHttpResponseWriterTest {
	private static final String TEST_FILES_DIR = "src/test/resources/response_examples";
	private static final Path OK_200_WITH_BODY = Paths.get(TEST_FILES_DIR, "200-OK-with-body.txt");
	private static final Path BAD_REQUEST_400_WITH_BODY = Paths.get(TEST_FILES_DIR, "400-Bad-Request-with-body.txt");
	private static final Path SERVER_ERROR_500_WITHOUT_BODY = Paths.get(TEST_FILES_DIR, "500-Server-Error-without-body.txt");
	
	private HttpResponseWriter responseWriter;
	
	@Before
	public void setup() {
		responseWriter = new DefaultHttpResponseWriter();
	}

	@Test
	public void writes200_OKResponseWithBody() throws Exception {
		ReadableHttpResponse response = create200OKResponse();
		StringWriter writer = new StringWriter();
		String expectedContent = getContentFrom(OK_200_WITH_BODY);
		
		responseWriter.writeHttpResponse(new WriterOutputStream(writer, StandardCharsets.UTF_8), response);
		
		String actualContent = writer.toString();
		assertThat(actualContent, equalTo(expectedContent));
	}
	
	@Test
	public void writes400_BadRequestResponseWithBody() throws Exception {
		ReadableHttpResponse response = create400BadRequestResponse();
		StringWriter writer = new StringWriter();
		String expectedContent = getContentFrom(BAD_REQUEST_400_WITH_BODY);
		
		responseWriter.writeHttpResponse(new WriterOutputStream(writer, StandardCharsets.UTF_8), response);
		
		String actualContent = writer.toString();
		assertThat(actualContent, equalTo(expectedContent));
	}
	
	@Test
	public void writes500_ServerErrorResponseWithoutBody() throws Exception {
		ReadableHttpResponse response = create500ServerErrorResponse();
		StringWriter writer = new StringWriter();
		String expectedContent = getContentFrom(SERVER_ERROR_500_WITHOUT_BODY);
		
		responseWriter.writeHttpResponse(new WriterOutputStream(writer, StandardCharsets.UTF_8), response);
		
		String actualContent = writer.toString();
		assertThat(actualContent, containsString(expectedContent));
	}

	private static ReadableHttpResponse create200OKResponse() {
		ReadableHttpResponse response = new DefaultReadableHttpResponse();
		response.setStatus(200);
		setHeaders(response);
		setBody(response);
		return response;
	}
	
	private static ReadableHttpResponse create400BadRequestResponse() {
		ReadableHttpResponse response = new DefaultReadableHttpResponse();
		response.setStatus(400);
		setHeaders(response);
		setBody(response);
		return response;
	}
	
	private static ReadableHttpResponse create500ServerErrorResponse() {
		ReadableHttpResponse response = new DefaultReadableHttpResponse();
		response.setStatus(500);
		setHeaders(response);
		response.setHeader("Content-Length", 0);
		return response;
	}

	private static void setBody(ReadableHttpResponse response) {
		response.setBody("email=welcome%40devstudy.net&password=&number=5&text=Simple+Text&url=http%3A%2F%2Fdevstudy.net");
	}

	private static void setHeaders(ReadableHttpResponse response) {
		response.setHeader("Date", "Wed, 12 Oct 2016 14:36:12 +0300");
		response.setHeader("Server", "Devstudy HTTP server");
		response.setHeader("Content-Language", "en");
		response.setHeader("Connection", "close");
		response.setHeader("Content-Type", "text/plain");
		response.setHeader("Content-Length", "94");
	}
	
	private static String getContentFrom(Path path) throws IOException {
		List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
		return allLines.stream().collect(Collectors.joining("\r\n"));
	}

}

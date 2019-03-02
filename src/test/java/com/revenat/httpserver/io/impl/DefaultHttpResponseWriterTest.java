package com.revenat.httpserver.io.impl;

import static com.revenat.httpserver.io.impl.TestPaths.BAD_REQUEST_400_WITH_BODY;
import static com.revenat.httpserver.io.impl.TestPaths.OK_200_WITH_BODY;
import static com.revenat.httpserver.io.impl.TestPaths.SERVER_ERROR_500_WITHOUT_BODY;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.output.WriterOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.revenat.httpserver.io.config.HttpResponseWriter;
import com.revenat.httpserver.io.config.HttpServerConfig;
import com.revenat.httpserver.io.config.ReadableHttpResponse;

@RunWith(MockitoJUnitRunner.class)
public class DefaultHttpResponseWriterTest {
	@Mock
	private HttpServerConfig config;

	private HttpResponseWriter responseWriter;

	@Before
	public void setup() {
		responseWriter = new DefaultHttpResponseWriter(config);
		when(config.getStatusMessage(Mockito.anyInt())).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				int statusCode = invocation.getArgument(0);
				if (statusCode == 200) {
					return "OK";
				} else if (statusCode == 400) {
					return "Bad Request";
				} else if (statusCode == 500) {
					return "Internal Server Error";
				}
				return "Unsupported status code";
			}
		});
	};

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
		ReadableHttpResponse response = createEmptyResponse();
		response.setStatus(200);
		setHeaders(response);
		setBody(response);
		return response;
	}

	private static ReadableHttpResponse create400BadRequestResponse() {
		ReadableHttpResponse response = createEmptyResponse();
		response.setStatus(400);
		setHeaders(response);
		setBody(response);
		return response;
	}

	private static ReadableHttpResponse create500ServerErrorResponse() {
		ReadableHttpResponse response = createEmptyResponse();
		response.setStatus(500);
		setHeaders(response);
		response.setHeader("Content-Length", 0);
		return response;
	}

	private static void setBody(ReadableHttpResponse response) {
		response.setBody(
				"email=welcome%40devstudy.net&password=&number=5&text=Simple+Text&url=http%3A%2F%2Fdevstudy.net");
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

	private static ReadableHttpResponse createEmptyResponse() {
		return new DefaultReadableHttpResponse();
	}
}

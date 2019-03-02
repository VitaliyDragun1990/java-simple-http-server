package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.config.HttpRequestParser;
import com.revenat.httpserver.io.exception.HttpVersionNotSupportedException;
import com.revenat.httpserver.io.exception.MethodNotAllowedException;

public class DefaultHttpRequestParserTest {
	private static final String DEFAULT_REMOTE_ADDRESS = "localhost";
	private static final String TEST_FILES_DIR = "src/test/resources/request_examples";
	private static final Path GET_SIMPLE = Paths.get(TEST_FILES_DIR, "get-simple.txt");
	private static final Path HEAD_SIMPLE = Paths.get(TEST_FILES_DIR, "head-simple.txt");
	private static final Path POST_SIMPLE = Paths.get(TEST_FILES_DIR, "post-simple.txt");
	private static final Path POST_WITH_EMPTY_BODY = Paths.get(TEST_FILES_DIR, "post-with-empty-body.txt");
	private static final Path POST_WITH_EMPTY_BODY_WITHOUT_CONTENT_LENGTH =
			Paths.get(TEST_FILES_DIR, "post-with-empty-body-and-without-content-length.txt");
	private static final Path POST_WITH_BODY_WITHOUT_CONTENT_LENGTH =
			Paths.get(TEST_FILES_DIR, "post-with-body-without-content-length.txt");
	private static final Path GET_INVALID_HTTP_VERSION = Paths.get(TEST_FILES_DIR, "get-invalid-http-version.txt");
	private static final Path UNSUPPORTED_METHOD = Paths.get(TEST_FILES_DIR, "method-not-allowed.txt");
	private static final Path GET_HEADERS_NEW_LINE = Paths.get(TEST_FILES_DIR, "get-headers-new-line.txt");
	private static final Path GET_HEADERS_CASE_INSENSITIVE = Paths.get(TEST_FILES_DIR,
			"get-headers-case-insensitive.txt");
	private static final Path GET_SIMPLE_PARAMS = Paths.get(TEST_FILES_DIR, "get-with-simple-params.txt");
	private static final Path GET_DECODED_PARAMS = Paths.get(TEST_FILES_DIR, "get-with-decoded-params.txt");
	private static final Path GET_DUPLICATE_PARAMS = Paths.get(TEST_FILES_DIR, "get-with-duplicate-params.txt");

	private HttpRequestParser parser;

	@Before
	public void setup() {
		parser = new DefaultHttpRequestParser();
	}

	@Test
	public void parsesGetHttpRequestWithStartingLine() throws Exception {
		InputStream requetsInputStream = Files.newInputStream(GET_SIMPLE);

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		assertThat(request.getStartingLine(), equalTo("GET /index.html HTTP/1.1"));
	}

	@Test
	public void parsesGetHttpRequestWithMethod() throws Exception {
		InputStream requetsInputStream = Files.newInputStream(GET_SIMPLE);

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		assertThat(request.getMethod(), equalTo("GET"));
	}

	@Test
	public void parsesGetHttpRequestWithUri() throws Exception {
		InputStream requetsInputStream = Files.newInputStream(GET_SIMPLE);

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		assertThat(request.getUri(), equalTo("/index.html"));
	}

	@Test
	public void parsesGetHttpRequestWithHttpVersion() throws Exception {
		InputStream requetsInputStream = Files.newInputStream(GET_SIMPLE);

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		assertThat(request.getHttpVersion(), equalTo("HTTP/1.1"));
	}

	@Test
	public void parsesGetHttpRequestWithRemoteAddress() throws Exception {
		InputStream requetsInputStream = Files.newInputStream(GET_SIMPLE);

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		assertThat(request.getRemoteAddress(), equalTo(DEFAULT_REMOTE_ADDRESS));
	}

	@Test
	public void parsesGetHttpRequestWithHeaders() throws Exception {
		InputStream requetsInputStream = Files.newInputStream(GET_SIMPLE);

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		assertThat(request.getHeaders().size(), equalTo(4));
		assertThat(request.getHeaders(), hasEntry("Host", "localhost"));
		assertThat(request.getHeaders(), hasEntry("User-Agent",
				"Mozilla/5.0 (X11; U; Linux i686; ru; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5"));
		assertThat(request.getHeaders(), hasEntry("Accept", "text/html"));
		assertThat(request.getHeaders(), hasEntry("Connection", "close"));
	}

	@Test
	public void parsesGetHttpRequestWithHeaderOccupiesSeveralLines() throws Exception {
		InputStream requetsInputStream = Files.newInputStream(GET_HEADERS_NEW_LINE);

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		assertThat(request.getHeaders().size(), equalTo(3));
		assertThat(request.getHeaders(), hasEntry("Host", "localhost"));
		assertThat(request.getHeaders(), hasEntry("Content-Type",
				"text/html;\r\n" + "              charset=windows-1251\r\n" + " charset=windows-3333"));
		assertThat(request.getHeaders(), hasEntry("Accept", "text/html"));
	}

	@Test
	public void parsesHttpGetRequestWithSimpleParameters() throws Exception {
		InputStream requetsInputStream = Files.newInputStream(GET_SIMPLE_PARAMS);

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		Map<String, String> params = request.getParameters();
		assertThat(params.size(), equalTo(2));
		assertThat(params, hasEntry("param1", "value1"));
		assertThat(params, hasEntry("param2", "true"));
	}

	@Test
	public void parsesGetHttpGetRequestWithDecodedParameters() throws Exception {
		InputStream requetsInputStream = Files.newInputStream(GET_DECODED_PARAMS);

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		Map<String, String> params = request.getParameters();
		assertThat(params.size(), equalTo(6));
		assertThat(params, hasEntry("email", "welcome@devstudy.net"));
		assertThat(params, hasEntry("password", ""));
		assertThat(params, hasEntry("text", "Simple Text"));
		assertThat(params, hasEntry("url", "http://devstudy.net"));
		assertThat(params, hasEntry("p", "test&qwerty?ty=u"));
	}

	@Test
	public void parsesGetHttpGetRequestWithDuplicateParameters() throws Exception {
		InputStream requetsInputStream = Files.newInputStream(GET_DUPLICATE_PARAMS);

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		Map<String, String> params = request.getParameters();
		assertThat(params.size(), equalTo(2));
		assertThat(params, hasEntry("param1", "value1,value2"));
		assertThat(params, hasEntry("param2", "true"));
	}

	@Test
	public void parsesGetHttpRequestWithCaseInsensitiveHeaders() throws Exception {
		InputStream requetsInputStream = Files.newInputStream(GET_HEADERS_CASE_INSENSITIVE);

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		assertThat(request.getHeaders().size(), equalTo(4));
		assertThat(request.getHeaders(), hasEntry("Host", "localhost"));
		assertThat(request.getHeaders(), hasEntry("User-Agent",
				"Mozilla/5.0 (X11; U; Linux i686; ru; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5"));
		assertThat(request.getHeaders(), hasEntry("Accept", "text/html"));
		assertThat(request.getHeaders(), hasEntry("Connection", "close"));
	}
	
	@Test
	public void parsesHeadHttpRequestWithoutHeaders() throws Exception {
		InputStream requetsInputStream = Files.newInputStream(HEAD_SIMPLE);

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);
		
		assertThat(request.getMethod(), equalTo("HEAD"));
		assertThat(request.getUri(), equalTo("/index.html"));
		assertThat(request.getHttpVersion(), equalTo("HTTP/1.1"));
		assertThat(request.getHeaders().size(), equalTo(0));
	}
	
	@Test
	public void parsesPostHttpRequestWithBodyAndContentLengthHeader() throws Exception {
		InputStream requetsInputStream = Files.newInputStream(POST_SIMPLE);

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);
		
		Map<String, String> headers = request.getHeaders();
		Map<String, String> params = request.getParameters();
		assertThat(request.getMethod(), equalTo("POST"));
		assertThat(headers.size(), equalTo(5));
		assertThat(headers, hasEntry("Content-Length", "94"));
		assertThat(params.size(), equalTo(5));
		assertThat(params, hasEntry("email", "welcome@devstudy.net"));
		assertThat(params, hasEntry("password", ""));
		assertThat(params, hasEntry("text", "Simple Text"));
		assertThat(params, hasEntry("url", "http://devstudy.net"));
		assertThat(params, hasEntry("number", "5"));
	}
	
	@Test
	public void parsesPostHttpRequestWithEmptyBodyAndZeroContentLengthHeader() throws Exception {
		InputStream requetsInputStream = Files.newInputStream(POST_WITH_EMPTY_BODY);

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);
		
		Map<String, String> headers = request.getHeaders();
		Map<String, String> params = request.getParameters();
		assertThat(request.getMethod(), equalTo("POST"));
		assertThat(headers.size(), equalTo(5));
		assertThat(headers, hasEntry("Content-Length", "0"));
		assertThat(params.size(), equalTo(0));
	}
	
	@Test
	public void parsesPostHttpRequestWithEmptyBodyAndWithoutContentLengthHeader() throws Exception {
		InputStream requetsInputStream = Files.newInputStream(POST_WITH_EMPTY_BODY_WITHOUT_CONTENT_LENGTH);

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);
		
		Map<String, String> headers = request.getHeaders();
		Map<String, String> params = request.getParameters();
		assertThat(request.getMethod(), equalTo("POST"));
		assertThat(headers.size(), equalTo(4));
		assertThat(params.size(), equalTo(0));
	}
	
	@Test
	public void parsesPostHttpRequestWithBodyAndWithoutContentLengthHeader() throws Exception {
		InputStream requetsInputStream = Files.newInputStream(POST_WITH_BODY_WITHOUT_CONTENT_LENGTH);

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);
		
		Map<String, String> headers = request.getHeaders();
		Map<String, String> params = request.getParameters();
		assertThat(request.getMethod(), equalTo("POST"));
		assertThat(headers.size(), equalTo(4));
		assertThat(params.size(), equalTo(0));
	}
	
	@Test(expected = HttpVersionNotSupportedException.class)
	public void throwsExceptionIfHttpVersionNotSupported() throws Exception {
		InputStream requestInputStream = Files.newInputStream(GET_INVALID_HTTP_VERSION);
		
		parser.parseHttpRequest(requestInputStream, DEFAULT_REMOTE_ADDRESS);
		
	}
	
	@Test(expected = MethodNotAllowedException.class)
	public void throwsExceptionIfMethodNotSupported() throws Exception {
		InputStream requestInputStream = Files.newInputStream(UNSUPPORTED_METHOD);
		
		parser.parseHttpRequest(requestInputStream, DEFAULT_REMOTE_ADDRESS);
		
	}

}

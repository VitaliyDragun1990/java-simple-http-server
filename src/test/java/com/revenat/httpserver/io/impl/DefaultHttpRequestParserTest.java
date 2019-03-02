package com.revenat.httpserver.io.impl;

import static com.revenat.httpserver.io.impl.TestPaths.GET_DECODED_PARAMS;
import static com.revenat.httpserver.io.impl.TestPaths.GET_DUPLICATE_PARAMS;
import static com.revenat.httpserver.io.impl.TestPaths.GET_HEADERS_CASE_INSENSITIVE;
import static com.revenat.httpserver.io.impl.TestPaths.GET_HEADERS_NEW_LINE;
import static com.revenat.httpserver.io.impl.TestPaths.GET_INVALID_HTTP_VERSION;
import static com.revenat.httpserver.io.impl.TestPaths.GET_SIMPLE;
import static com.revenat.httpserver.io.impl.TestPaths.GET_SIMPLE_PARAMS;
import static com.revenat.httpserver.io.impl.TestPaths.HEAD_SIMPLE;
import static com.revenat.httpserver.io.impl.TestPaths.POST_SIMPLE;
import static com.revenat.httpserver.io.impl.TestPaths.POST_WITH_BODY_WITHOUT_CONTENT_LENGTH;
import static com.revenat.httpserver.io.impl.TestPaths.POST_WITH_EMPTY_BODY;
import static com.revenat.httpserver.io.impl.TestPaths.POST_WITH_EMPTY_BODY_WITHOUT_CONTENT_LENGTH;
import static com.revenat.httpserver.io.impl.TestPaths.UNSUPPORTED_METHOD;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.config.HttpRequestParser;
import com.revenat.httpserver.io.exception.HttpVersionNotSupportedException;
import com.revenat.httpserver.io.exception.MethodNotAllowedException;

public class DefaultHttpRequestParserTest {
	private static final String DEFAULT_REMOTE_ADDRESS = "localhost";

	private HttpRequestParser parser;

	@Before
	public void setup() {
		parser = new DefaultHttpRequestParser();
	}
	
	private static InputStream fromRequest(Path requestPath) throws IOException {
		return Files.newInputStream(requestPath);
	}

	@Test
	public void parsesGetHttpRequestWithStartingLine() throws Exception {
		try (InputStream requetsInputStream = fromRequest(GET_SIMPLE)) {

			HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

			assertThat(request.getStartingLine(), equalTo("GET /index.html HTTP/1.1"));
		}
	}

	@Test
	public void parsesGetHttpRequestWithMethod() throws Exception {
		try (InputStream requetsInputStream = fromRequest(GET_SIMPLE)) {

			HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

			assertThat(request.getMethod(), equalTo("GET"));
		}
	}

	@Test
	public void parsesGetHttpRequestWithUri() throws Exception {
		try (InputStream requetsInputStream = fromRequest(GET_SIMPLE)) {

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		assertThat(request.getUri(), equalTo("/index.html"));
		}
	}

	@Test
	public void parsesGetHttpRequestWithHttpVersion() throws Exception {
		try (InputStream requetsInputStream = fromRequest(GET_SIMPLE)) {

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		assertThat(request.getHttpVersion(), equalTo("HTTP/1.1"));
		}
	}

	@Test
	public void parsesGetHttpRequestWithRemoteAddress() throws Exception {
		try (InputStream requetsInputStream = fromRequest(GET_SIMPLE)) {

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		assertThat(request.getRemoteAddress(), equalTo(DEFAULT_REMOTE_ADDRESS));
		}
	}

	@Test
	public void parsesGetHttpRequestWithHeaders() throws Exception {
		try (InputStream requetsInputStream = fromRequest(GET_SIMPLE)) {

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		assertThat(request.getHeaders().size(), equalTo(4));
		assertThat(request.getHeaders(), hasEntry("Host", "localhost"));
		assertThat(request.getHeaders(), hasEntry("User-Agent",
				"Mozilla/5.0 (X11; U; Linux i686; ru; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5"));
		assertThat(request.getHeaders(), hasEntry("Accept", "text/html"));
		assertThat(request.getHeaders(), hasEntry("Connection", "close"));
		}
	}

	@Test
	public void parsesGetHttpRequestWithHeaderOccupiesSeveralLines() throws Exception {
		try (InputStream requetsInputStream = fromRequest(GET_HEADERS_NEW_LINE)) {

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		assertThat(request.getHeaders().size(), equalTo(3));
		assertThat(request.getHeaders(), hasEntry("Host", "localhost"));
		assertThat(request.getHeaders(), hasEntry("Content-Type",
				"text/html;" + "charset=windows-1251;" + "charset=windows-3333"));
		assertThat(request.getHeaders(), hasEntry("Accept", "text/html"));
		}
	}

	@Test
	public void parsesHttpGetRequestWithSimpleParameters() throws Exception {
		try (InputStream requetsInputStream = fromRequest(GET_SIMPLE_PARAMS)) {

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		Map<String, String> params = request.getParameters();
		assertThat(params.size(), equalTo(2));
		assertThat(params, hasEntry("param1", "value1"));
		assertThat(params, hasEntry("param2", "true"));
		}
	}

	@Test
	public void parsesGetHttpGetRequestWithDecodedParameters() throws Exception {
		try (InputStream requetsInputStream = fromRequest(GET_DECODED_PARAMS)) {

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		Map<String, String> params = request.getParameters();
		assertThat(params.size(), equalTo(6));
		assertThat(params, hasEntry("email", "welcome@devstudy.net"));
		assertThat(params, hasEntry("password", ""));
		assertThat(params, hasEntry("text", "Simple Text"));
		assertThat(params, hasEntry("url", "http://devstudy.net"));
		assertThat(params, hasEntry("p", "test&qwerty?ty=u"));
		}
	}

	@Test
	public void parsesGetHttpGetRequestWithDuplicateParameters() throws Exception {
		try (InputStream requetsInputStream = fromRequest(GET_DUPLICATE_PARAMS)) {
		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		Map<String, String> params = request.getParameters();
		assertThat(params.size(), equalTo(2));
		assertThat(params, hasEntry("param1", "value1,value2"));
		assertThat(params, hasEntry("param2", "true"));
		}
	}

	@Test
	public void parsesGetHttpRequestWithCaseInsensitiveHeaders() throws Exception {
		try (InputStream requetsInputStream = fromRequest(GET_HEADERS_CASE_INSENSITIVE)) {

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		assertThat(request.getHeaders().size(), equalTo(4));
		assertThat(request.getHeaders(), hasEntry("Host", "localhost"));
		assertThat(request.getHeaders(), hasEntry("User-Agent",
				"Mozilla/5.0 (X11; U; Linux i686; ru; rv:1.9b5) Gecko/2008050509 Firefox/3.0b5"));
		assertThat(request.getHeaders(), hasEntry("Accept", "text/html"));
		assertThat(request.getHeaders(), hasEntry("Connection", "close"));
		}
	}

	@Test
	public void parsesHeadHttpRequestWithoutHeaders() throws Exception {
		try (InputStream requetsInputStream = fromRequest(HEAD_SIMPLE)) {

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		assertThat(request.getMethod(), equalTo("HEAD"));
		assertThat(request.getUri(), equalTo("/index.html"));
		assertThat(request.getHttpVersion(), equalTo("HTTP/1.1"));
		assertThat(request.getHeaders().size(), equalTo(0));
		}
	}

	@Test
	public void parsesPostHttpRequestWithBodyAndContentLengthHeader() throws Exception {
		try (InputStream requetsInputStream = fromRequest(POST_SIMPLE)) {

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
	}

	@Test
	public void parsesPostHttpRequestWithEmptyBodyAndZeroContentLengthHeader() throws Exception {
		try (InputStream requetsInputStream = fromRequest(POST_WITH_EMPTY_BODY)) {

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		Map<String, String> headers = request.getHeaders();
		Map<String, String> params = request.getParameters();
		assertThat(request.getMethod(), equalTo("POST"));
		assertThat(headers.size(), equalTo(5));
		assertThat(headers, hasEntry("Content-Length", "0"));
		assertThat(params.size(), equalTo(0));
		}
	}

	@Test
	public void parsesPostHttpRequestWithEmptyBodyAndWithoutContentLengthHeader() throws Exception {
		try (InputStream requetsInputStream = fromRequest(POST_WITH_EMPTY_BODY_WITHOUT_CONTENT_LENGTH)) {

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		Map<String, String> headers = request.getHeaders();
		Map<String, String> params = request.getParameters();
		assertThat(request.getMethod(), equalTo("POST"));
		assertThat(headers.size(), equalTo(4));
		assertThat(params.size(), equalTo(0));
		}
	}

	@Test
	public void parsesPostHttpRequestWithBodyAndWithoutContentLengthHeader() throws Exception {
		try (InputStream requetsInputStream = fromRequest(POST_WITH_BODY_WITHOUT_CONTENT_LENGTH)) {

		HttpRequest request = parser.parseHttpRequest(requetsInputStream, DEFAULT_REMOTE_ADDRESS);

		Map<String, String> headers = request.getHeaders();
		Map<String, String> params = request.getParameters();
		assertThat(request.getMethod(), equalTo("POST"));
		assertThat(headers.size(), equalTo(4));
		assertThat(params.size(), equalTo(0));
		}
	}

	@Test(expected = HttpVersionNotSupportedException.class)
	public void throwsExceptionIfHttpVersionNotSupported() throws Exception {
		try (InputStream requestsInputStream = fromRequest(GET_INVALID_HTTP_VERSION)) {

		parser.parseHttpRequest(requestsInputStream, DEFAULT_REMOTE_ADDRESS);
		}

	}

	@Test(expected = MethodNotAllowedException.class)
	public void throwsExceptionIfMethodNotSupported() throws Exception {
		try (InputStream requestsInputStream = fromRequest(UNSUPPORTED_METHOD)) {

		parser.parseHttpRequest(requestsInputStream, DEFAULT_REMOTE_ADDRESS);
		}

	}
}

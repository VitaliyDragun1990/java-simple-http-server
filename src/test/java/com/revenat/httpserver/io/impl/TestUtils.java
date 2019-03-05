package com.revenat.httpserver.io.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class TestUtils {
	/**
	 * Root directories for test files
	 */
	private static final String REQUEST_TEST_FILES_DIR = "src/test/resources/request_examples";
	private static final String RESPONSE_TEST_FILES_DIR = "src/test/resources/response_examples";
	
	/**
	 * HTTP request test examples
	 */
	public static final Path GET_SIMPLE = Paths.get(REQUEST_TEST_FILES_DIR, "get-simple.txt");
	public static final Path HEAD_SIMPLE = Paths.get(REQUEST_TEST_FILES_DIR, "head-simple.txt");
	public static final Path POST_SIMPLE = Paths.get(REQUEST_TEST_FILES_DIR, "post-simple.txt");
	public static final Path POST_WITH_EMPTY_BODY = Paths.get(REQUEST_TEST_FILES_DIR, "post-with-empty-body.txt");
	public static final Path POST_WITH_EMPTY_BODY_WITHOUT_CONTENT_LENGTH = Paths.get(REQUEST_TEST_FILES_DIR,
			"post-with-empty-body-and-without-content-length.txt");
	public static final Path POST_WITH_BODY_WITHOUT_CONTENT_LENGTH = Paths.get(REQUEST_TEST_FILES_DIR,
			"post-with-body-without-content-length.txt");
	public static final Path GET_INVALID_HTTP_VERSION = Paths.get(REQUEST_TEST_FILES_DIR, "get-invalid-http-version.txt");
	public static final Path UNSUPPORTED_METHOD = Paths.get(REQUEST_TEST_FILES_DIR, "method-not-allowed.txt");
	public static final Path GET_HEADERS_NEW_LINE = Paths.get(REQUEST_TEST_FILES_DIR, "get-headers-new-line.txt");
	public static final Path GET_HEADERS_CASE_INSENSITIVE = Paths.get(REQUEST_TEST_FILES_DIR,
			"get-headers-case-insensitive.txt");
	public static final Path GET_SIMPLE_PARAMS = Paths.get(REQUEST_TEST_FILES_DIR, "get-with-simple-params.txt");
	public static final Path GET_DECODED_PARAMS = Paths.get(REQUEST_TEST_FILES_DIR, "get-with-decoded-params.txt");
	public static final Path GET_DUPLICATE_PARAMS = Paths.get(REQUEST_TEST_FILES_DIR, "get-with-duplicate-params.txt");
	
	/**
	 * HTTP response test examples
	 */
	public static final Path OK_200_WITH_BODY = Paths.get(RESPONSE_TEST_FILES_DIR, "200-OK-with-body.txt");
	public static final Path BAD_REQUEST_400_WITH_BODY = Paths.get(RESPONSE_TEST_FILES_DIR, "400-Bad-Request-with-body.txt");
	public static final Path SERVER_ERROR_500_WITHOUT_BODY = Paths.get(RESPONSE_TEST_FILES_DIR, "500-Server-Error-without-body.txt");
	
	/**
	 * Test properties for DefaultHttpServerConfig
	 */
	public static final String ROOT_PATH = "src/test/resources";
	public static final String STATUSES_PROPS_RESOURCE = "statuses.properties";
	public static final String SERVER_PROPS_RESOURCE = "server.properties";
	public static final String MIME_PROPS_RESOURCE = "mime-types.properties";
	public final Properties MIME_PROPERTIES = createMimeProperties();
	public final Properties STATUSES_PROPERTIES = createStatusesProperties();
	public final Properties SERVER_PROPERTIES = createServerProperties();
	
	public static Properties createServerProperties() {
		Properties props = new Properties();
		props.put("server.port", "80");
		props.put("server.name", "Test server");
		props.put("server.thread.count", "0");
		props.put("webapp.static.dir.root", "src/test/resources");
		props.put("webapp.static.expires.extensions", "css,js,eot,svg,ttf,woff,woff2");
		props.put("webapp.static.expires.days", "7");
		props.put("db.datasource.enabled", "true");
		props.put("db.datasource.driver", "org.postgresql.Driver");
		props.put("db.datasource.url", "jdbc:postgresql://localhost/test");
		props.put("db.datasource.username", "test");
		props.put("db.datasource.password", "test");
		props.put("db.datasource.pool.initSize", "2");
		props.put("db.datasource.pool.maxSize", "3");

		return props;
	}

	public static Properties createStatusesProperties() {
		Properties props = new Properties();
		props.put("200", "OK");
		props.put("400", "Bad Request");
		props.put("404", "Not Found");
		props.put("405", "Method Not Allowed");
		props.put("500", "Internal Server Error");
		props.put("505", "HTTP Version Not Supported");
		return props;
	}

	public static Properties createMimeProperties() {
		Properties props = new Properties();
		props.put("123", "application/vnd.lotus-1-2-3");
		return props;
	}
}

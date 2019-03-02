package com.revenat.httpserver.io.impl;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class TestPaths {
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
}

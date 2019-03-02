package com.revenat.httpserver.io.utils;

import static java.util.Objects.requireNonNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utility class that contains helper methods to facilitate work with Http
 * request and response specific tasks.
 * 
 * @author Vitaly Dragun
 *
 */
public final class HttpUtils {
	private static final String CONTENT_LENGTH = "content-length: ";

	/**
	 * Normalizes specified header name to common standard.
	 * 
	 * @param name header name to normalize
	 * @return normalized header name
	 */
	public static String normalizeHeaderName(String name) {
		requireNonNull(name, "Header name can not be null");

		StringBuilder headerName = new StringBuilder(name.trim());
		for (int i = 0; i < headerName.length(); i++) {
			char ch = headerName.charAt(i);
			if (i == 0) {
				toUpper(ch, i, headerName);
			} else if (ch == '-' && i < headerName.length() - 1) {
				toUpper(headerName.charAt(i + 1), i + 1, headerName);
				i++;
			} else {
				if (Character.isUpperCase(ch)) {
					headerName.setCharAt(i, Character.toLowerCase(ch));
				}
			}
		}
		return headerName.toString();
	}

	private static void toUpper(char ch, int index, StringBuilder headerName) {
		if (Character.isLowerCase(ch)) {
			headerName.setCharAt(index, Character.toUpperCase(ch));
		}

	}

	/**
	 * Reads starting line and headers for HTTP request from specified input stream
	 * and returns result as a string.
	 */
	public static String readStartingLineAndHeaders(InputStream inputStream) throws IOException {
		requireNonNull(inputStream, "Input stream can not be null");

		ByteArray buffer = new ByteArray();
		while (!buffer.isEmptyLine()) {
			int read = inputStream.read();
			if (read == -1) {
				throw new EOFException("InputStream is closed");
			}
			buffer.add((byte) read);
		}
		return new String(buffer.toArray(), StandardCharsets.UTF_8);
	}

	private HttpUtils() {
	}

	/**
	 * Extracts and returns value of the {@code Content-Length} header.
	 * 
	 * @param startingLineAndHeaders string which contains starting line and headers
	 *                               of the HTTP request
	 * @return value of the {@code Content-Length} header, or {@code 0} if such
	 *         header is absent.
	 */
	public static int getContentLengthValue(String startingLineAndHeaders) {
		requireNonNull(startingLineAndHeaders, "Staring line and headers string can not be null");

		int contentLengthIndex = startingLineAndHeaders.toLowerCase().indexOf(CONTENT_LENGTH);
		if (contentLengthIndex != -1) {
			int startCutIndex = contentLengthIndex + CONTENT_LENGTH.length();
			int endCutIndex = startingLineAndHeaders.indexOf("\r\n", startCutIndex);
			if (endCutIndex == -1) {
				endCutIndex = startingLineAndHeaders.length();
			}
			return Integer.parseInt(startingLineAndHeaders.substring(startCutIndex, endCutIndex).trim());
		}
		return 0;
	}

	/**
	 * Reads HTTP request body with size equal to specified {@code contentLength}
	 * parameter from specified {@link InputStream} instance.
	 * 
	 * @param inputStream   input stream to read body from
	 * @param contentLength precise length of the request body to read
	 * @return byte array with body content or empty one if specified
	 *         {@code contentLength} parameter is equals to {@code 0}
	 * @throws IOException
	 */
	public static byte[] readBody(InputStream inputStream, int contentLength) throws IOException {
		requireNonNull(inputStream, "Input stream can not be null");

		ByteArray body = new ByteArray();
		while (contentLength > 0) {
			byte[] buffer = new byte[contentLength];
			int readCount = inputStream.read(buffer);
			body.add(buffer, 0, readCount);
			contentLength -= readCount;
		}
		return body.toArray();
	}
}

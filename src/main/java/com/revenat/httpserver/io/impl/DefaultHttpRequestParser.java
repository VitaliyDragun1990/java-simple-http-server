package com.revenat.httpserver.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.revenat.httpserver.io.Constants;
import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.config.HttpRequestParser;
import com.revenat.httpserver.io.exception.BadRequestException;
import com.revenat.httpserver.io.exception.HttpServerException;
import com.revenat.httpserver.io.exception.HttpVersionNotSupportedException;
import com.revenat.httpserver.io.exception.MethodNotAllowedException;
import com.revenat.httpserver.io.utils.DataUtils;
import com.revenat.httpserver.io.utils.HttpUtils;

/**
 * Reference implementation of the {@link HttpRequestParser}
 * 
 * @author Vitaly Dragun
 *
 */
class DefaultHttpRequestParser implements HttpRequestParser {

	@Override
	public HttpRequest parseHttpRequest(InputStream inputStream, String remoteAddress)
			throws IOException, HttpServerException {
		try {
			ParsedRequest request = parseInputStream(inputStream);
			return convertParsedRequestToHttpRequest(request, remoteAddress);
		} catch (HttpServerException e) {
			throw e;
		} catch (RuntimeException e) {
			throw new BadRequestException("Can not parse HTTP request: " + e.getMessage(), e, null);
		}
	}

	private static ParsedRequest parseInputStream(InputStream inputStream) throws IOException {
		String startingLineAndHeaders = HttpUtils.readStartingLineAndHeaders(inputStream);
		int contentLength = HttpUtils.getContentLengthValue(startingLineAndHeaders);
		byte[] body = HttpUtils.readBody(inputStream, contentLength);
		return new ParsedRequest(startingLineAndHeaders, body);
	}

	private static HttpRequest convertParsedRequestToHttpRequest(ParsedRequest request, String remoteAddress)
			throws IOException {
		// Parse starting line: e.g. GET /index.html HTTP/1.1
		String[] startingLineData = request.startingLine.split(" ");
		String method = startingLineData[0];
		validateMethod(request.startingLine, method);
		String uri = startingLineData[1];
		String httpVersion = startingLineData[2];
		validateHttpVersion(request.startingLine, httpVersion);
		// Parse headers: e.g. Host: localhost
		Map<String, String> headers = parseHeaders(request.headersLines);
		// Parse message body / URI parameters
		ProcessedUri processedUri = extractParametersIfPresent(method, uri, request.messageBody);

		return new DefaultHttpRequest(method, processedUri.uri, httpVersion, remoteAddress, headers,
				processedUri.parameters);
	}

	private static void validateHttpVersion(String startingLine, String httpVersion) {
		if (!Constants.HTTP_VERSION.equals(httpVersion)) {
			throw new HttpVersionNotSupportedException(
					"Http server currently supports only " + Constants.HTTP_VERSION + " protocol", startingLine);
		}
	}

	private static void validateMethod(String startingLine, String method) {
		if (Constants.ALLOWED_METHODS.stream().noneMatch(allowedMethod -> allowedMethod.equalsIgnoreCase(method))) {
			throw new MethodNotAllowedException(method, startingLine);
		}
	}

	private static Map<String, String> parseHeaders(List<String> headersLines) {
		Map<String, String> headers = new LinkedHashMap<>();
		String lastParsedHeader = null;

		for (String headerLine : headersLines) {
			lastParsedHeader = parseHeader(lastParsedHeader, headers, headerLine);
		}

		return headers;
	}

	private static String parseHeader(String lastParsedHeader, Map<String, String> headers, String headerLine) {
		if (headerLine.charAt(0) == ' ') {
			String value = headers.get(lastParsedHeader) + headerLine.trim();
			headers.put(lastParsedHeader, value);
			return lastParsedHeader;
		} else {
			int delimeterIndex = headerLine.indexOf(':');
			String name = HttpUtils.normalizeHeaderName(headerLine.substring(0, delimeterIndex));
			String value = headerLine.substring(delimeterIndex + 1).trim();
			headers.put(name, value);
			return name;
		}
	}

	private static ProcessedUri extractParametersIfPresent(String method, String uri, String messageBody)
			throws IOException {
		Map<String, String> parameters = Collections.emptyMap();

		if (Constants.GET.equalsIgnoreCase(method) || Constants.HEAD.equalsIgnoreCase(method)) {
			parameters = extractParametersFromUri(uri);
		} else if (Constants.POST.equalsIgnoreCase(method) && !messageBody.isEmpty()) {
			parameters = getParameters(messageBody);
		}

		return new ProcessedUri(uri, parameters);
	}

	private static Map<String, String> extractParametersFromUri(String uri) throws UnsupportedEncodingException {
		int delimeterIndex = uri.indexOf('?');
		if (delimeterIndex != -1) {
			return getParameters(uri.substring(delimeterIndex + 1));
		}
		return Collections.emptyMap();
	}

	private static Map<String, String> getParameters(String paramString) throws UnsupportedEncodingException {
		Map<String, String> parameters = new HashMap<>();
		String[] paramLines = paramString.split("&");
		for (String paramLine : paramLines) {
			String[] items = paramLine.split("=");
			// If empty value for parameter
			if (items.length == 1) {
				items = new String[] { items[0], "" };
			}
			String paramName = items[0];
			String paramValue = URLDecoder.decode(items[1], StandardCharsets.UTF_8.name());
			parameters.merge(paramName, paramValue, (oldVal, newVal) -> {
				if (oldVal.contains(newVal)) {
					return oldVal;
				}
				return oldVal.concat(",").concat(newVal);
			});
		}
		return parameters;
	}

	/**
	 * Component that represents first stage of parsing HTTP request. Holds main
	 * HTTP request parts as strings (startingLine, headers, messageBody).
	 * 
	 * @author Vitaly Dragun
	 *
	 */
	private static class ParsedRequest {
		private final String startingLine;
		private final List<String> headersLines;
		private final String messageBody;

		ParsedRequest(String startingLineAndHeaders, byte[] body) {
			List<String> lines = DataUtils.convertToLineList(startingLineAndHeaders);
			this.startingLine = lines.remove(0);
			if (lines.isEmpty()) {
				this.headersLines = Collections.emptyList();
			} else {
				this.headersLines = Collections.unmodifiableList(lines);
			}
			this.messageBody = new String(body, StandardCharsets.UTF_8);
		}
	}

	/**
	 * Component that represents HTTP request URI with optional request parameters.
	 * 
	 * @author Vitaly Dragun
	 *
	 */
	private static class ProcessedUri {
		private final String uri;
		private final Map<String, String> parameters;

		ProcessedUri(String uri, Map<String, String> parameters) {
			this.uri = getExactUriString(uri);
			this.parameters = parameters;
		}

		private static String getExactUriString(String uri) {
			int delimeterIndex = uri.indexOf('?');
			if (delimeterIndex != -1) {
				return uri.substring(0, delimeterIndex);
			} else {
				return uri;
			}
		}

	}

}

package com.revenat.httpserver.io.impl.defaults;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revenat.httpserver.io.Constants;
import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.config.HttpRequestParser;
import com.revenat.httpserver.io.exception.HttpServerException;
import com.revenat.httpserver.io.exception.HttpVersionNotSupportedException;
import com.revenat.httpserver.io.exception.MethodNotAllowedException;
import com.revenat.httpserver.io.utils.ByteArray;

/**
 * Default implementation of the {@link HttpRequestParser}
 * 
 * @author Vitaly Dragun
 *
 */
class DefaultHttpRequestParser implements HttpRequestParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHttpRequestParser.class);
	private static final char WHITESPACE = ' ';
	private static final char PARAMS_NAME_VALUE_DELIMITER = '=';
	private static final String PARAMS_DELIMITER = "&";
	private static final char URI_PARAMS_DELIMITER = '?';
	private static final char HEADER_NAME_VALUES_DELIMITER = ':';
	private static final String SUPPORTED_HTTP_VERSION = "HTTP/1.1";
	private static final Collection<String> ALLOWED_METHODS = Constants.ALLOWED_METHODS;
	private static final String CONTENT_LENGTH_HEADER = "content-length: ";
	private static final String NEW_LINE_SYMBOL = "\r\n";

	@Override
	public HttpRequest parseHttpRequest(InputStream inputStream, String remoteAddress)
			throws IOException, HttpServerException {
		DefaultHttpRequest httpRequest = new DefaultHttpRequest();
		httpRequest.setRemoteAddress(remoteAddress);

		try (InputStream in = inputStream) {

			String startingLine = getStartingLine(in);
			String headers = getHeaders(in);
			ByteArray body = getBody(in, getContentLength(headers));

			populateRequest(httpRequest, startingLine, headers, body);
		}

		return httpRequest;
	}

	private static String getStartingLine(InputStream in) throws IOException {
		ByteArray buffer = new ByteArray();
		int read = 0;
		while ((read = in.read()) != -1) {
			buffer.add((byte) read);
			if (buffer.isLineFeed()) {
				break;
			}
		}
		return new String(buffer.toArray(), StandardCharsets.UTF_8).trim();
	}

	private static String getHeaders(InputStream in) throws IOException {
		ByteArray buffer = new ByteArray();
		int read = 0;
		while ((read = in.read()) != -1) {
			buffer.add((byte) read);
			if (buffer.isEmptyLine()) {
				break;
			}
		}
		return new String(buffer.toArray(), StandardCharsets.UTF_8).trim();
	}

	private static int getContentLength(String headers) {
		int contentLengthIndex = headers.toLowerCase().indexOf(CONTENT_LENGTH_HEADER);

		if (contentLengthIndex != -1) {
			int startCutIndex = contentLengthIndex + CONTENT_LENGTH_HEADER.length();
			int contentLengthEndIndex = headers.indexOf(NEW_LINE_SYMBOL, startCutIndex);
			int endCutIndex = contentLengthEndIndex != -1 ? contentLengthEndIndex : headers.length();
			String contentLength = headers.substring(startCutIndex, endCutIndex).trim();
			try {
				return Integer.parseInt(contentLength);
			} catch (NumberFormatException e) {
				LOGGER.warn("Invalid 'Content-Length' header value. Should be integer number, but was {}",
						contentLength);
			}
		}
		return 0;
	}

	private static ByteArray getBody(InputStream in, int contentLength) throws IOException {
		ByteArray bodyContent = new ByteArray();

		while (contentLength > 0) {
			byte[] buffer = new byte[contentLength];
			int readCount = in.read(buffer);
			bodyContent.add(buffer, 0, readCount);
			contentLength -= readCount;
		}

		return bodyContent;
	}

	private static void populateRequest(DefaultHttpRequest request, String startingLine, String headers,
			ByteArray body) {
		String method = parseMethod(startingLine);
		String uri = parseUri(startingLine);
		String httpVersion = parseHttpVersion(startingLine);
		Map<String, String> allHeaders = parseHeaders(headers);
		Map<String, String> parameters = parseParametersFromUri(uri);
		parameters.putAll(parseParametersFromBody(body));

		request.setStartingLine(startingLine);
		request.setMethod(method);
		request.setUri(uri);
		request.setHttpVersion(httpVersion);
		request.setHeaders(allHeaders);
		request.setParameters(parameters);
	}

	private static String parseMethod(String startingLine) {
		int endIndex = startingLine.indexOf(WHITESPACE);
		String method = startingLine.substring(0, endIndex).trim();
		return requireAllowedMethod(method, startingLine);
	}

	private static String requireAllowedMethod(String method, String startingLine) {
		if (!ALLOWED_METHODS.contains(method)) {
			throw new MethodNotAllowedException(method, startingLine);
		}
		return method;
	}

	private static String parseUri(String startingLine) {
		int startIndex = startingLine.indexOf(WHITESPACE);
		int endIndex = startingLine.indexOf(WHITESPACE, startIndex + 1);
		return startingLine.substring(startIndex, endIndex).trim();
	}

	private static String parseHttpVersion(String startingLine) {
		int startIndex = startingLine.lastIndexOf(WHITESPACE);
		String httpVersion = startingLine.substring(startIndex + 1).trim();
		return requireCorrectHttpVersion(httpVersion, startingLine);
	}

	private static String requireCorrectHttpVersion(String httpVersion, String startingLine) {
		if (!SUPPORTED_HTTP_VERSION.equals(httpVersion)) {
			throw new HttpVersionNotSupportedException(httpVersion, startingLine);
		}
		return httpVersion;
	}

	private static Map<String, String> parseHeaders(String headers) {
		List<String> headerLines = getHeaderLines(headers);
		return getHeaders(headerLines);
	}

	private static List<String> getHeaderLines(String headersString) {
		List<String> headerLines = new ArrayList<>();

		int startIndex = 0;
		int endIndex = 0;
		while (endIndex != -1) {
			while (true) {
				endIndex = headersString.indexOf(NEW_LINE_SYMBOL, startIndex);
				while (endIndex + 2 < headersString.length() && headersString.charAt(endIndex + 2) == WHITESPACE) {
					endIndex = headersString.indexOf(NEW_LINE_SYMBOL, endIndex + 2);
				}
				if (endIndex != -1) {
					headerLines.add(headersString.substring(startIndex, endIndex));
					startIndex = endIndex + 2;
				}
				break;
			}

		}
		if (startIndex < headersString.length()) {
			headerLines.add(headersString.substring(startIndex));
		}

		return headerLines;
	}

	private static Map<String, String> getHeaders(List<String> headerLines) {
		Map<String, String> headers = new LinkedHashMap<>();
		for (String headerLine : headerLines) {
			int delimeterIndex = headerLine.indexOf(HEADER_NAME_VALUES_DELIMITER);
			String headerName = transformToStandardForm(headerLine.substring(0, delimeterIndex).trim());
			String headerValue = headerLine.substring(delimeterIndex + 1).trim();
			headers.put(headerName, headerValue);
		}
		return headers;
	}

	private static String transformToStandardForm(String headerName) {
		headerName = makeFirstLetterUppercase(headerName.toLowerCase());
		char delimeter = '-';
		int delimeterIndex = 0;

		while (delimeterIndex != -1) {
			delimeterIndex = headerName.indexOf(delimeter, delimeterIndex + 1);
			if (delimeterIndex != -1 && delimeterIndex > 0 && delimeterIndex < headerName.length() - 1) {
				String firtsPart = makeFirstLetterUppercase(headerName.substring(0, delimeterIndex));
				String secondPart = makeFirstLetterUppercase(headerName.substring(delimeterIndex + 1));
				headerName = firtsPart + delimeter + secondPart;

			}
		}

		return headerName;
	}

	private static String makeFirstLetterUppercase(String line) {
		if (line.length() > 0) {
			return line.substring(0, 1).toUpperCase() + line.substring(1);
		}
		return line;
	}

	private static Map<String, String> parseParametersFromUri(String uri) {
		String parametersString = parseParametersString(uri);
		return getParameters(parametersString);
	}

	private static Map<String, String> parseParametersFromBody(ByteArray body) {
		String bodyContent = new String(body.toArray(), StandardCharsets.UTF_8);
		return getParameters(bodyContent);
	}

	private static String parseParametersString(String uri) {
		int startParamsIndex = uri.indexOf(URI_PARAMS_DELIMITER);
		return startParamsIndex != -1 ? uri.substring(startParamsIndex + 1) : "";
	}

	private static Map<String, String> getParameters(String parametersString) {
		Map<String, String> params = new LinkedHashMap<>();
		if (!parametersString.isEmpty()) {
			String[] paramLines = parametersString.split(PARAMS_DELIMITER);
			for (String paramLine : paramLines) {
				int delimeterindex = paramLine.indexOf(PARAMS_NAME_VALUE_DELIMITER);
				String paramName = paramLine.substring(0, delimeterindex).trim();
				String paramValue = delimeterindex + 1 < paramLine.length()
						? paramLine.substring(delimeterindex + 1).trim()
						: "";
				try {
					params.merge(paramName, URLDecoder.decode(paramValue, StandardCharsets.UTF_8.name()),
							(oldVal, newVal) -> {
								if (oldVal.contains(newVal)) {
									return oldVal;
								}
								return oldVal.concat(",").concat(newVal);
							});
				} catch (UnsupportedEncodingException e) {
					LOGGER.warn("Error during encoding parameters values", e);
				}
			}
		}
		return params;
	}
}

package com.revenat.httpserver.io.impl;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.revenat.httpserver.io.config.ReadableHttpResponse;
import com.revenat.httpserver.io.exception.HttpServerException;
import com.revenat.httpserver.io.utils.HttpUtils;

/**
 * Reference implementation of the {@link ReadableHttpResponse}
 * 
 * @author Vitaly Dragun
 *
 */
class DefaultReadableHttpResponse implements ReadableHttpResponse {
	private final Map<String, String> headers;
	private byte[] body;
	private int status;
	
	DefaultReadableHttpResponse() {
		this.status = 200;
		this.headers = new LinkedHashMap<>();
		this.body = new byte[0];
	}
	
	@Override
	public void setStatus(int status) {
		this.status = status;

	}

	@Override
	public void setHeader(String name, Object value) {
		requireNonNull(name, "Header name can not be null");
		requireNonNull(value, "Header value can not be null");
		name = HttpUtils.normalizeHeaderName(name);
		
		if (value instanceof TemporalAccessor) {
			headers.put(name, DateTimeFormatter.RFC_1123_DATE_TIME.format((TemporalAccessor) value));
		} else if (value instanceof FileTime) {
			ZonedDateTime dateTime = ZonedDateTime.ofInstant(((FileTime) value).toInstant(), ZoneId.systemDefault());
			headers.put(name, DateTimeFormatter.RFC_1123_DATE_TIME.format(dateTime));
		} else {
			headers.put(name, String.valueOf(value));
		}

	}

	@Override
	public void setBody(String content) {
		requireNonNull(content, "Body content can not be null ");
		this.body = content.getBytes(StandardCharsets.UTF_8);

	}

	@Override
	public void setBody(InputStream in) {
		try {
			requireNonNull(in, "InputStream can not be null");
			this.body = IOUtils.toByteArray(in);
		} catch (IOException e) {
			throw new HttpServerException("Can not set HTTP response body from InputStream: " + e.getMessage(), e);
		}
	}

	@Override
	public void setBody(Reader reader) {
		try {
			requireNonNull(reader, "Reader can not be null");
			this.body = IOUtils.toByteArray(reader, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new HttpServerException("Can not set HTTP response body from Reader: " + e.getMessage(), e);
		}
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public Map<String, String> getHeaders() {
		return Collections.unmodifiableMap(headers);
	}

	@Override
	public byte[] getBody() {
		return Arrays.copyOf(body, body.length);
	}

	@Override
	public boolean isBodyEmpty() {
		return getBodyLength() == 0;
	}

	@Override
	public int getBodyLength() {
		return body.length;
	}

}

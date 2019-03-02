package com.revenat.httpserver.io.impl.defaults;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.input.ReaderInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revenat.httpserver.io.config.ReadableHttpResponse;
import com.revenat.httpserver.io.exception.HttpServerException;
import com.revenat.httpserver.io.utils.ByteArray;

/**
 * Default implementation of the {@link ReadableHttpResponse}
 * 
 * @author Vitaly Dragun
 *
 */
class DefaultReadableHttpResponse implements ReadableHttpResponse {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultReadableHttpResponse.class);

	private int status;
	private Map<String, String> headers;
	private byte[] body;

	protected DefaultReadableHttpResponse() {
		status = 200;
		headers = new LinkedHashMap<>();
		body = new byte[0];
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public void setHeader(String name, Object value) {
		requireNonNull(name, "Header name can not be null");
		requireNonNull(value, "Header value can not be null");

		headers.put(name, value.toString());
	}

	@Override
	public void setBody(String content) {
		requireNonNull(content, "Body content can not be null");
		body = content.getBytes(StandardCharsets.UTF_8);

	}

	@Override
	public void setBody(InputStream in) {
		requireNonNull(in, "Input stream to read body from can not be null");
		readBodyFrom(in);
	}

	@Override
	public void setBody(Reader reader) {
		requireNonNull(reader, "Reader stream to read body from can not be null");
		readBodyFrom(new ReaderInputStream(reader, StandardCharsets.UTF_8));
	}

	private void readBodyFrom(InputStream inputStream) {
		try (InputStream in = inputStream) {
			ByteArray buffer = new ByteArray();
			int read = 0;
			while ((read = in.read()) != -1) {
				buffer.add((byte) read);
			}
			body = buffer.toArray();
		} catch (IOException e) {
			LOGGER.error("Error occurred during reading HttpResponse body content from input stream.", e);
			throw new HttpServerException(
					"Error occured during reading HttpResponse body content from" + " input stream", e);
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
		return body.length == 0;
	}

	@Override
	public int getBodyLength() {
		return body.length;
	}

}

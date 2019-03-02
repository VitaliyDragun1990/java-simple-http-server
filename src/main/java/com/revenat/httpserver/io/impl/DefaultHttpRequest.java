package com.revenat.httpserver.io.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.revenat.httpserver.io.HttpRequest;

class DefaultHttpRequest implements HttpRequest {
	private String startingLine;
	private String method;
	private String uri;
	private String httpVersion;
	private String remoteAddress;
	private Map<String, String> headers;
	private Map<String, String> parameters;

	public DefaultHttpRequest() {
		headers = new LinkedHashMap<>();
		parameters = new LinkedHashMap<>();
	}

	@Override
	public String getStartingLine() {
		return startingLine;
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public String getUri() {
		return uri;
	}

	@Override
	public String getHttpVersion() {
		return httpVersion;
	}

	@Override
	public String getRemoteAddress() {
		return remoteAddress;
	}

	@Override
	public Map<String, String> getHeaders() {
		return Collections.unmodifiableMap(headers);
	}

	@Override
	public Map<String, String> getParameters() {
		return Collections.unmodifiableMap(parameters);
	}

	void setStartingLine(String startingLine) {
		this.startingLine = startingLine;
	}

	void setMethod(String method) {
		this.method = method;
	}

	void setUri(String uri) {
		this.uri = uri;
	}

	void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}

	void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	void setHeaders(Map<String, String> headers) {
		this.headers.putAll(headers);
	}

	void setParameters(Map<String, String> parameters) {
		this.parameters.putAll(parameters);
	}

}

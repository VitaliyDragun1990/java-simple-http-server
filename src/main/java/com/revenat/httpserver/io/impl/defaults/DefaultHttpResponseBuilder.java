package com.revenat.httpserver.io.impl.defaults;

import com.revenat.httpserver.io.config.HttpResponseBuilder;
import com.revenat.httpserver.io.config.ReadableHttpResponse;

class DefaultHttpResponseBuilder implements HttpResponseBuilder {
	private final DateTimeProvider dateTimeProvider;

	public DefaultHttpResponseBuilder(DateTimeProvider dateProvider) {
		this.dateTimeProvider = dateProvider;
	}

	@Override
	public ReadableHttpResponse buildNewHttpResponse() {
		ReadableHttpResponse response = new DefaultReadableHttpResponse();
		
		setStarterHeaders(response);
		
		return response;
	}

	private void setStarterHeaders(ReadableHttpResponse response) {
		response.setHeader("Date", dateTimeProvider.getDateTimeString());
		response.setHeader("Server", "Devstudy HTTP server");
		response.setHeader("Content-Language", "en");
		response.setHeader("Connection", "close");
		response.setHeader("Content-Type", "text/html");
		
	}

	@Override
	public void prepareHttpResponse(ReadableHttpResponse response, boolean clearBody) {
		response.setHeader("Content-Length", String.valueOf(response.getBodyLength()));
		if (clearBody) {
			response.setBody("");
		}
	}

}

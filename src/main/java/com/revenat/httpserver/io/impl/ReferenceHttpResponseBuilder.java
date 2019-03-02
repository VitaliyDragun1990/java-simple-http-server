package com.revenat.httpserver.io.impl;

import static java.util.Objects.requireNonNull;

import com.revenat.httpserver.io.config.HttpResponseBuilder;
import com.revenat.httpserver.io.config.HttpServerConfig;
import com.revenat.httpserver.io.config.ReadableHttpResponse;

/**
 * Reference implementation of the {@link HttpResponseBuilder}
 * 
 * @author Vitaly Dragun
 *
 */
class ReferenceHttpResponseBuilder extends AbstractHttpConfigurableComponent implements HttpResponseBuilder {
	private static final String DEFAULT_CONTENT_TYPE = "text/html";
	protected final DateTimeProvider dateTimeProvider;

	ReferenceHttpResponseBuilder(HttpServerConfig httpServerConfig, DateTimeProvider dateTimeProvider) {
		super(httpServerConfig);
		this.dateTimeProvider = requireNonNull(dateTimeProvider, "DateTimeProvider can not be null");
	}

	@Override
	public ReadableHttpResponse buildNewHttpResponse() {
		ReadableHttpResponse response = createReadableHttpResponse();

		response.setHeader("Date", dateTimeProvider.getCurrentDateTime());
		response.setHeader("Server", httpServerConfig.getServerInfo().getName());
		response.setHeader("Content-Language", "en");
		response.setHeader("Connection", "close");
		response.setHeader("Content-Type", DEFAULT_CONTENT_TYPE);

		return response;
	}

	protected ReadableHttpResponse createReadableHttpResponse() {
		return new ReferenceReadableHttpResponse();
	}

	@Override
	public void prepareHttpResponse(ReadableHttpResponse response, boolean clearBody) {
		if (response.getStatus() >= 400 && response.isBodyEmpty()) {
			// TODO: implement adding error page as body
		}
		setContentLength(response);
		if (clearBody) {
			clearBody(response);
		}
	}

	private static void setContentLength(ReadableHttpResponse response) {
		response.setHeader("Content-Length", response.getBodyLength());

	}

	private static void clearBody(ReadableHttpResponse response) {
		response.setBody("");
	}

}

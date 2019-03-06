package com.revenat.httpserver.io.impl;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import com.revenat.httpserver.io.config.HttpResponseBuilder;
import com.revenat.httpserver.io.config.HttpServerConfig;
import com.revenat.httpserver.io.config.ReadableHttpResponse;
import com.revenat.httpserver.io.utils.DataUtils;

/**
 * Reference implementation of the {@link HttpResponseBuilder}
 * 
 * @author Vitaly Dragun
 *
 */
class DefaultHttpResponseBuilder extends AbstractHttpConfigurableComponent implements HttpResponseBuilder {
	private static final String STATUS_MESSAGE = "STATUS-MESSAGE";
	private static final String STATUS_CODE = "STATUS-CODE";
	private static final String ERROR_TEMPLATE = "error.html";
	private static final String DEFAULT_CONTENT_TYPE = "text/html";
	protected final DateTimeProvider dateTimeProvider;

	DefaultHttpResponseBuilder(HttpServerConfig httpServerConfig, DateTimeProvider dateTimeProvider) {
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
		return new DefaultReadableHttpResponse();
	}

	@Override
	public void prepareHttpResponse(ReadableHttpResponse response, boolean clearBody) {
		if (response.getStatus() >= 400 && response.isBodyEmpty()) {
			setDefaultResponseErrorBody(response);
		}
		setContentLength(response);
		if (clearBody) {
			clearBody(response);
		}
	}

	private void setDefaultResponseErrorBody(ReadableHttpResponse response) {
		Map<String, Object> templateArgs = DataUtils.buildMap(new Object[][] {
			{ STATUS_CODE, response.getStatus() },
			{ STATUS_MESSAGE, httpServerConfig.getStatusMessage(response.getStatus()) }
		});
		
		String content = httpServerConfig.getHttpServerContext()
				.getHtmlTemplateManager().pocessTemplate(ERROR_TEMPLATE, templateArgs);
		response.setBody(content);
		
	}

	private static void setContentLength(ReadableHttpResponse response) {
		response.setHeader("Content-Length", response.getBodyLength());

	}

	private static void clearBody(ReadableHttpResponse response) {
		response.setBody("");
	}

}

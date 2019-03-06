package com.revenat.httpserver.io.handler;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.revenat.httpserver.io.Constants;
import com.revenat.httpserver.io.HttpHandler;
import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.HttpResponse;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.utils.DataUtils;

/**
 * Implementation of the {@link HttpHandler} whose job is to provide a client with
 * the current information about HTTP server state.
 * 
 * @author Vitaly Dragun
 *
 */
public class ServerInfoHttpHandler implements HttpHandler {

	private static final String UNLIMITED_COUNT = "UNLIMITED";
	private static final String SUPPORTED_RESPONSE_STATUSES = "SUPPORTED-RESPONSE-STATUSES";
	private static final String SUPPORTED_REQUEST_METHODS = "SUPPORTED-REQUEST-METHODS";
	private static final String THREAD_COUNT = "THREAD-COUNT";
	private static final String SERVER_PORT = "SERVER-PORT";
	private static final String SERVER_NAME = "SERVER-NAME";
	private static final String SERVER_INFO_TEMPLATE = "server-info.html";

	@Override
	public void handle(HttpServerContext context, HttpRequest request, HttpResponse response) throws IOException {
		requireNonNull(context, "context can not be null");
		requireNonNull(request, "request can not be null");
		requireNonNull(response, "response can not be null");
		
		if (Constants.GET.equals(request.getMethod())) {
			Map<String, Object> templateData = getTemplateData(context);
			response.setBody(context.getHtmlTemplateManager().pocessTemplate(SERVER_INFO_TEMPLATE, templateData));
		} else {
			response.setStatus(400);
		}

	}

	private Map<String, Object> getTemplateData(HttpServerContext context) {
		int threadCount = context.getServerInfo().getThreadCount();
		return DataUtils.buildMap(new Object[][] {
			{ SERVER_NAME, context.getServerInfo().getName() },
			{ SERVER_PORT, context.getServerInfo().getPort() },
			{ THREAD_COUNT, threadCount == 0 ? UNLIMITED_COUNT : threadCount },
			{ SUPPORTED_REQUEST_METHODS, context.getSupportedRequestMethods() },
			{ SUPPORTED_RESPONSE_STATUSES, getSupportedResponseStatuses(context) }
		});
	}

	private StringBuilder getSupportedResponseStatuses(HttpServerContext context) {
		StringBuilder html = new StringBuilder();
		Map<Object, Object> statuses = new TreeMap<>(context.getSupportedResponseStatuses());
		for (Map.Entry<Object, Object> status : statuses.entrySet()) {
			html.append(status.getKey()).append(" [").append(status.getValue()).append("]<br />");
		}
		return html;
	}

}

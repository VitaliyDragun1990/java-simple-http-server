package com.revenat.httpserver.io.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.revenat.httpserver.io.Constants;
import com.revenat.httpserver.io.config.HttpResponseWriter;
import com.revenat.httpserver.io.config.HttpServerConfig;
import com.revenat.httpserver.io.config.ReadableHttpResponse;

/**
 * Reference implementation of the {@link HttpResponseWriter}
 * 
 * @author Vitaly Dragun
 *
 */
class DefaultHttpResponseWriter extends AbstractHttpConfigurableComponent implements HttpResponseWriter {

	DefaultHttpResponseWriter(HttpServerConfig httpServerConfig) {
		super(httpServerConfig);
	}

	@Override
	public void writeHttpResponse(OutputStream out, ReadableHttpResponse response) throws IOException {
		PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8)));
		writeStartingLine(writer, response);
		writeHeaders(writer, response);
		writer.println();
		writer.flush();
		writeMessageBody(out, response);
	}

	protected void writeStartingLine(PrintWriter out, ReadableHttpResponse response) {
		String httpVersion = Constants.HTTP_VERSION;
		int statusCode = response.getStatus();
		String statusMessage = httpServerConfig.getStatusMessage(statusCode);
		out.println(String.format("%s %s %s", httpVersion, statusCode, statusMessage));
	}

	protected void writeHeaders(PrintWriter out, ReadableHttpResponse response) {
		for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
			out.println(String.format("%s: %s", header.getKey(), header.getValue()));
		}
	}

	protected void writeMessageBody(OutputStream out, ReadableHttpResponse response) throws IOException {
		if (!response.isBodyEmpty()) {
			out.write(response.getBody());
			out.flush();
		}
	}
}

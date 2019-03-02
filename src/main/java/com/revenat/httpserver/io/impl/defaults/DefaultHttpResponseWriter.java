package com.revenat.httpserver.io.impl.defaults;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.revenat.httpserver.io.config.HttpResponseWriter;
import com.revenat.httpserver.io.config.ReadableHttpResponse;

class DefaultHttpResponseWriter implements HttpResponseWriter {
	private static final String HTTP_VERSION = "HTTP/1.1";
	private static final Map<Integer, String> STATUS_MESSAGES = new HashMap<>();
	
	static {
		STATUS_MESSAGES.put(200, "OK");
		STATUS_MESSAGES.put(400, "Bad Request");
		STATUS_MESSAGES.put(500, "Internal Server Error");
	}

	@Override
	public void writeHttpResponse(OutputStream out, ReadableHttpResponse response) throws IOException {
		try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
			String startingLine = getStartingLine(response);
			writer.println(startingLine);
			
			Map<String, String> headers = response.getHeaders();
			for (Entry<String, String> header : headers.entrySet()) {
				writer.println(header.getKey() + ": " + header.getValue());
			}
			
			writer.println();
			writer.flush();
			
			byte[] body = response.getBody();
			out.write(body);
		}

	}

	private static String getStartingLine(ReadableHttpResponse response) {
		return HTTP_VERSION + " " + response.getStatus() + " " + STATUS_MESSAGES.get(response.getStatus());
	}

}

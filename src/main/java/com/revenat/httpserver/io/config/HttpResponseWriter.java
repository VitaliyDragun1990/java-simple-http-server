package com.revenat.httpserver.io.config;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Component responsible for writing {@link ReadableHttpResponse} object
 * into the clients {@link OutputStream}
 * @author Vitaly Dragun
 *
 */
public interface HttpResponseWriter {

	void writeHttpResponse(OutputStream out, ReadableHttpResponse response)
		throws IOException;
}

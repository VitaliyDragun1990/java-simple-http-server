package com.revenat.httpserver.io.exception;

/**
 * Exception that represents an error during HTTP server configuration process.
 * 
 * @author Vitaly Dragun
 *
 */
public class HttpServerConfigException extends HttpServerException {
	private static final long serialVersionUID = 3238138783597370196L;

	public HttpServerConfigException(String message) {
		super(message);
	}

	public HttpServerConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpServerConfigException(Throwable cause) {
		super(cause);
	}

}

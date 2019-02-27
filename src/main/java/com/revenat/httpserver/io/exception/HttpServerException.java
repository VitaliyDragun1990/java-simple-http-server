package com.revenat.httpserver.io.exception;

/**
 * Generic top level class for all HTTP server exceptions.
 * 
 * @author Vitaly Dragun
 *
 */
public class HttpServerException extends RuntimeException {
	private static final long serialVersionUID = -8400794657539572243L;
	private int statusCode = 500;

	public HttpServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpServerException(String message) {
		super(message);
	}

	public HttpServerException(Throwable cause) {
		super(cause);
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

}

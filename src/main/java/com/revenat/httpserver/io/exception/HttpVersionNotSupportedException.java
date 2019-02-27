package com.revenat.httpserver.io.exception;

/**
 * Exception that represents that request processing failed because of
 * unsupported HTTP protocol version.
 * 
 * @author Vitaly Dragun
 *
 */
public class HttpVersionNotSupportedException extends AbstractRequestParseFailedException {
	private static final long serialVersionUID = -7631772002512857519L;

	public HttpVersionNotSupportedException(String message, String startingLine) {
		super(message, startingLine);
		setStatusCode(505);
	}

}

package com.revenat.httpserver.io.exception;

/**
 * Exception which represents that request processing failed because of
 * {@code Bad Request} (status 400)
 * @author Vitaly Dragun
 *
 */
public class BadRequestException extends AbstractRequestParseFailedException {
	private static final long serialVersionUID = 5247622838366326135L;

	public BadRequestException(String message, Throwable cause, String startingLine) {
		super(message, cause, startingLine);
		setStatusCode(400);
	}

}

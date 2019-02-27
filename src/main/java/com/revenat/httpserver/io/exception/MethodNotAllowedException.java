package com.revenat.httpserver.io.exception;

import com.revenat.httpserver.io.Constants;

public class MethodNotAllowedException extends AbstractRequestParseFailedException {
	private static final long serialVersionUID = 5275389554048808760L;

	public MethodNotAllowedException(String method, String startingLine) {
		super("Only " + Constants.ALLOWED_METHODS + " are supported. Current method is " + method, startingLine);
		setStatusCode(405);
	}


}

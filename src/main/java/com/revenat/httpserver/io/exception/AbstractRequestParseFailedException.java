package com.revenat.httpserver.io.exception;

import com.revenat.httpserver.io.HttpRequest;

/**
 * Abstract exception class that represents that request parsing process is failed
 * for some reason.
 * @author Vitaly Dragun
 *
 */
public abstract class AbstractRequestParseFailedException extends HttpServerException {
	private static final long serialVersionUID = 4097822315332731839L;

	/**
	 * Starting line from the {@link HttpRequest} which parsing failed.
	 */
	private final String startingLine;

	public AbstractRequestParseFailedException(String message, Throwable cause, String startingLine) {
		super(message, cause);
		this.startingLine = startingLine;
	}

	public AbstractRequestParseFailedException(String message, String startingLine) {
		super(message);
		this.startingLine = startingLine;
	}

	/**
	 * Returns {@link HttpRequest}'s starting line property.
	 * @return
	 */
	public String getStartingLine() {
		return startingLine;
	}

}

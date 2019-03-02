package com.revenat.httpserver.io.impl;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementation of the {@link DateTimeProvider} that returns date-time string
 * formatted in RFC-1123 format (such as {@code Tue, 3 Jun 2008 11:05:30 GMT} ).
 * @author Vitaly Dragun
 *
 */
public class RFC1123DateTimeProvider implements DateTimeProvider {

	@Override
	public String getDateTimeString() {
		return DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now());
	}

}

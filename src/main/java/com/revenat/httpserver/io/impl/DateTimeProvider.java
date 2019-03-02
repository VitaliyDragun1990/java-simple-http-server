package com.revenat.httpserver.io.impl;

import java.time.ZonedDateTime;

/**
 * Component that can produce date and time data in form of
 * {@link ZonedDateTime} instance.
 * 
 * @author Vitaly Dragun
 *
 */
interface DateTimeProvider {

	/**
	 * Returns instance of the {@link ZonedDateTime} with 
	 * data that was actual at the time of calling this method.
	 */
	ZonedDateTime getCurrentDateTime();
}

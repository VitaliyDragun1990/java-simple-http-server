package com.revenat.httpserver.io;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Contains useful constant values
 * @author Vitaly Dragun
 *
 */
public final class Constants {
	public static final String HTTP_VERSION = "HTTP/1.1";
	
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String HEAD = "HEAD";
	
	public static final List<String> ALLOWED_METHODS = Collections.unmodifiableList(Arrays.asList(GET, POST, HEAD));

	private Constants() {
	}

}

package com.revenat.httpserver.io.utils;

import static java.util.Objects.requireNonNull;

import java.util.LinkedList;
import java.util.List;

/**
 * Utility class with helper methods to work with lists and maps.
 * @author Vitaly Dragun
 *
 */
public final class DataUtils {

	/**
	 * Splits given string message using line feed sequence {@code '\r\n'}
	 * and returns result as {@link List} of strings
	 */
	public static List<String> convertToLineList(String message) {
		requireNonNull(message, "Message can not be null");
		
		List<String> lines = new LinkedList<>();
		int start = 0;
		for (int i = 1; i < message.length(); i++) {
			if (message.charAt(i) == '\n' && message.charAt(i- 1) == '\r') {
				String line = message.substring(start, i - 1);
				if (!line.isEmpty()) {
					lines.add(line);
				}
				start = i + 1;
			}
		}
		if (message.length() > 0) {
			String line = message.substring(start);
			if (!line.isEmpty()) {
				lines.add(line);
			}
		}
		
		return lines;
	}
	
	private DataUtils() {}
}

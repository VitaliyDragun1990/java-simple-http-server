package com.revenat.httpserver.io.utils;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Utility class with helper methods to work with lists and maps.
 * 
 * @author Vitaly Dragun
 *
 */
public final class DataUtils {

	/**
	 * Splits given string message using line feed sequence {@code '\r\n'} and
	 * returns result as {@link List} of strings
	 */
	public static List<String> convertToLineList(String message) {
		requireNonNull(message, "Message can not be null");

		List<String> lines = new LinkedList<>();
		int start = 0;
		for (int i = 1; i < message.length(); i++) {
			if (message.charAt(i) == '\n' && message.charAt(i - 1) == '\r') {
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

	/**
	 * Builds map from provided two-dimension array (table).
	 * For each row in that array uses first element (index 0) as key to map,
	 * second element (index 1) as value respectively.
	 * @param data {@code not null} two-dimension array of objects
	 * @return
	 */
	public static Map<String, Object> buildMap(Object[][] data) {
		requireNonNull(data, "Data can not be null");
		validateArrayDimension(data);
		
		Map<String, Object> map = new HashMap<>();

		for (Object[] row : data) {
			map.put(String.valueOf(row[0]), row[1]);
		}

		return Collections.unmodifiableMap(map);
	}

	private static void validateArrayDimension(Object[][] data) {
		for (int row = 0; row < data.length; row++) {
			if (data[row].length < 2) {
				throw new IllegalArgumentException("Each row in two-dimension data array should have"
						+ " at least 2 elements, but row " + row + " has less than 2");
			}
		}
		
	}

	private DataUtils() {
	}
}

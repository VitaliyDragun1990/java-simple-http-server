package com.revenat.httpserver.io.utils;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class DataUtilsTest {

	@Test
	public void correctlySplitsStringWithoutLineFeeds() throws Exception {
		String message = "Hello world";

		List<String> lines = DataUtils.convertToLineList(message);

		assertThat(lines, hasSize(1));
		assertThat(lines, contains(equalTo(message)));

	}

	@Test
	public void correctlySplitsStringWithLineFeeds() throws Exception {
		String message = "Hello world\r\nAnd greeting to Java";

		List<String> lines = DataUtils.convertToLineList(message);

		assertThat(lines, hasSize(2));
		assertThat(lines, contains(equalTo("Hello world"), equalTo("And greeting to Java")));
	}
	
	@Test
	public void returnsListWithLinesInCorrectOrder() throws Exception {
		String message = "Hello world\r\nAnd greeting to Java";

		List<String> lines = DataUtils.convertToLineList(message);

		assertThat(lines, hasSize(2));
		assertThat(lines, containsInRelativeOrder(equalTo("Hello world"), equalTo("And greeting to Java")));
	}

	@Test
	public void returnsEmptyListForEmptyMessage() throws Exception {
		String message = "";

		List<String> lines = DataUtils.convertToLineList(message);

		assertThat(lines, hasSize(0));

	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfMessageIsNull() throws Exception {
		DataUtils.convertToLineList(null);
	}
	
	@Test
	public void buildsMapFromTwoDimensionArray() throws Exception {
		Object[][] table = {{"key1", "value1"}, {"key2", "value2"}};
		
		Map<String, Object> map = DataUtils.buildMap(table);
		
		assertThat(map.size(), equalTo(2));
		assertThat(map, hasEntry("key1", "value1"));
		assertThat(map, hasEntry("key2", "value2"));
		
	}
	
	@Test
	public void buildsEmptyMapFromEmptyTwoDImensionArray() throws Exception {
		Map<String, Object> map = DataUtils.buildMap(new Object[][] {});
		
		assertThat(map.size(), equalTo(0));
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIftryToBuildMapFromNull() throws Exception {
		DataUtils.buildMap(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void throwsExceptionIfAnyRowInArrayToBuildMapFromHasLessThanTwoElements() throws Exception {
		Object[][] table = {{"key1", "value1"}, {"key2"}};
		
		DataUtils.buildMap(table);
	}

}

package com.revenat.httpserver.io.utils;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

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

}

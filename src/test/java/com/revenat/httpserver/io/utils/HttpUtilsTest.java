package com.revenat.httpserver.io.utils;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.EOFException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.input.ReaderInputStream;
import org.junit.Test;

public class HttpUtilsTest {
	private static final String STARTING_LINE = "GET / HTTP/1.1";
	private static final String LINE_FEED = "\r\n";
	private static final String HEADERS = "Header1: value1\r\n" + "Header2: value2";
	private static final String EMPTY_LINE = LINE_FEED + LINE_FEED;

	@Test
	public void normalizesHeaderNameFromAllLowerCase() throws Exception {
		String headerName = "content-length";

		assertThat(HttpUtils.normalizeHeaderName(headerName), equalTo("Content-Length"));
	}

	@Test
	public void normalizesHeaderNameFromAllUpperCase() throws Exception {
		String headerName = "CONTENT-LENGTH";

		assertThat(HttpUtils.normalizeHeaderName(headerName), equalTo("Content-Length"));
	}

	@Test
	public void normalizesHeaderNameFromMixedCase() throws Exception {
		String headerName = "cOnTEnT-LEnGth";

		assertThat(HttpUtils.normalizeHeaderName(headerName), equalTo("Content-Length"));
	}

	@Test
	public void normalizesComplexHeaderName() throws Exception {
		String headerName = "contenT-LENGTH-WitH-additiON";

		assertThat(HttpUtils.normalizeHeaderName(headerName), equalTo("Content-Length-With-Addition"));
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIftryToNormalizeNull() throws Exception {
		HttpUtils.normalizeHeaderName(null);
	}

	@Test
	public void readsStartingLineFromInputStream() throws Exception {
		String content = STARTING_LINE + EMPTY_LINE;

		String startingLine = HttpUtils
				.readStartingLineAndHeaders(new ReaderInputStream(new StringReader(content), StandardCharsets.UTF_8));

		assertThat(startingLine, equalTo(content));

	}

	@Test
	public void readsStartingLineWithHeadersFromInputStream() throws Exception {
		String content = STARTING_LINE + LINE_FEED + HEADERS + EMPTY_LINE;

		String startingLine = HttpUtils
				.readStartingLineAndHeaders(new ReaderInputStream(new StringReader(content), StandardCharsets.UTF_8));

		assertThat(startingLine, equalTo(content));

	}

	@Test(expected = EOFException.class)
	public void throwsExceptionIfNoContentInTheInputStream() throws Exception {
		String content = "";

		HttpUtils.readStartingLineAndHeaders(new ReaderInputStream(new StringReader(content), StandardCharsets.UTF_8));
	}

	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfTryToReadStartingLineAndHeadersfromNullInputStream() throws Exception {
		HttpUtils.readStartingLineAndHeaders(null);
	}

	@Test
	public void returnsContentLengthValue() throws Exception {
		String header = "Content-Length: 155";

		int contentLengthValue = HttpUtils.getContentLengthValue(header);

		assertThat(contentLengthValue, equalTo(155));

	}

	@Test
	public void returnsZeroIfNotContentLengthHeader() throws Exception {
		String header = "Some-Header: 155";

		int contentLengthValue = HttpUtils.getContentLengthValue(header);

		assertThat(contentLengthValue, equalTo(0));
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfLookForContentLengthHeaderInNullString() throws Exception {
		HttpUtils.getContentLengthValue(null);
	}

	@Test
	public void readsBodyFromInputStream() throws Exception {
		String body = "This is body content";
		int bodyLength = body.getBytes(StandardCharsets.UTF_8).length;

		byte[] result = HttpUtils.readBody(new ReaderInputStream(new StringReader(body), StandardCharsets.UTF_8),
				bodyLength);

		assertThat(result, equalTo(body.getBytes(StandardCharsets.UTF_8)));
	}

	@Test
	public void returnsEmptyArrayIfBodyIsEmpty() throws Exception {
		String body = "";
		int bodyLength = 0;

		byte[] result = HttpUtils.readBody(new ReaderInputStream(new StringReader(body), StandardCharsets.UTF_8),
				bodyLength);

		assertThat(result, equalTo(new byte[0]));
	}

	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfPassNullInputStreamtoReadBody() throws Exception {
		HttpUtils.readBody(null, 0);

	}
}

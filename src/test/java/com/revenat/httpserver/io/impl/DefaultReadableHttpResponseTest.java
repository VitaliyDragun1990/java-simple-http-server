package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.FileTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.io.input.ReaderInputStream;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.revenat.httpserver.io.config.ReadableHttpResponse;
import com.revenat.httpserver.io.exception.HttpServerException;

public class DefaultReadableHttpResponseTest {
	private static final ZonedDateTime DATE_NOW = ZonedDateTime.now();
	private static final FileTime FILE_TIME = FileTime.from(DATE_NOW.toInstant());
	private static final String DATE_TIME_STRING = DateTimeFormatter.RFC_1123_DATE_TIME.format(DATE_NOW);

	private ReadableHttpResponse response;
	
	@Before
	public void setup() {
		response = new DefaultReadableHttpResponse();
	}

	@Test
	public void containsEmptyBodyWhenCreated() throws Exception {
		assertThat(response.getBodyLength(), equalTo(0));
		assertThat(response.isBodyEmpty(), is(true));

	}

	@Test
	public void containsNoHeadersWhenCreated() throws Exception {
		Map<String, String> headers = response.getHeaders();

		assertThat(headers.size(), equalTo(0));
	}

	@Test
	public void contains200StatusWhenCreated() throws Exception {
		assertThat(response.getStatus(), equalTo(200));
	}
	
	@Test
	public void containsAddedHeaders() throws Exception {
		response.setHeader("name", "value");
		response.setHeader("name2", "value2");
		
		Map<String, String> headers = response.getHeaders();
		
		assertThat(headers.size(), equalTo(2));
		assertThat(headers, hasEntry("Name", "value"));
		assertThat(headers, hasEntry("Name2", "value2"));
	}
	
	@Test
	public void holdsHeadersInTheOrderTheyWereAdded() throws Exception {
		response.setHeader("Name", "value");
		response.setHeader("Name2", "value2");
		
		Map<String, String> headers = response.getHeaders();
		
		assertThat(headers.size(), equalTo(2));
		assertThat(headers, MapEntriesMatcher.containsEntriesInRelativeOrder(new Object[] {"Name", "value"},
				new Object[] {"Name2", "value2"}));
		
	}
	
	@Test
	public void setsHeaderValueInTheRigthFormatForDate() throws Exception {
		response.setHeader("Last-Modified", DATE_NOW);
		
		assertThat(response.getHeaders().get("Last-Modified"), equalTo(DATE_TIME_STRING));
	}
	
	@Test
	public void setsHeaderValueInTheRigthFormatForFileTime() throws Exception {
		response.setHeader("Last-Modified", FILE_TIME);
		
		assertThat(response.getHeaders().get("Last-Modified"), equalTo(DATE_TIME_STRING));
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfHeaderNameIsNull() throws Exception {
		response.setHeader(null, "value");
		
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfHeaderValueIsNull() throws Exception {
		response.setHeader("name", null);
		
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfPassNullStringToBody() throws Exception {
		response.setBody((String)null);
		
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfPassNullInputStreamToBody() throws Exception {
		response.setBody((InputStream)null);
		
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfPassNullReaderToBody() throws Exception {
		response.setBody((Reader)null);
		
	}
	
	@Test
	public void setsBodyFromString() throws Exception {
		String bodyContent = "Body content";
		
		response.setBody(bodyContent);
		
		byte[] body = response.getBody();
		assertThat(new String(body, StandardCharsets.UTF_8), equalTo(bodyContent));
		
	}
	
	@Test
	public void setsBodyContentFromInputStream() throws Exception {
		String bodyContent = "Body content";
		StringReader reader = new StringReader(bodyContent);
		
		
		response.setBody(new ReaderInputStream(reader, StandardCharsets.UTF_8));
		
		byte[] body = response.getBody();
		assertThat(new String(body, StandardCharsets.UTF_8), equalTo(bodyContent));
	}
	
	@Test
	public void setsBodyContentFromReader() throws Exception {
		String bodyContent = "Body content";
		StringReader reader = new StringReader(bodyContent);
		
		
		response.setBody(reader);
		
		byte[] body = response.getBody();
		assertThat(new String(body, StandardCharsets.UTF_8), equalTo(bodyContent));
	}
	
	@Test(expected = HttpServerException.class)
	public void throwsExceptionIfErrorOccursDuringReadingBodyFromInputStream() throws Exception {
		InputStream in = mock(InputStream.class, throwsIOExceptionForAllMethodCalls());
		
		response.setBody(in);
		
	}
	
	@Test(expected = HttpServerException.class)
	public void throwsExceptionIfErrorOccursDuringReadingBodyFromReader() throws Exception {
		Reader reader = mock(Reader.class, throwsIOExceptionForAllMethodCalls());
		
		response.setBody(reader);
		
	}

	private static Answer<?> throwsIOExceptionForAllMethodCalls() {
		return new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				throw new IOException("Something went wrong");
			}
		};
	}
	
	@SuppressWarnings("rawtypes")
	private static class MapEntriesMatcher extends TypeSafeMatcher<Map> {
		private final List<Object[]> entries = new LinkedList<>();
		
		private MapEntriesMatcher(Object[]... entries) {
			for (Object[] entry : entries) {
				if (entry.length != 2) {
					throw new IllegalArgumentException("Entry array should contains exactly two elements: key and value");
				}
				this.entries.add(entry);
			}
		}
		
		public static MapEntriesMatcher containsEntriesInRelativeOrder(Object[]... entries) {
			return new MapEntriesMatcher(entries);
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("Enrties in relative order");
			
		}

		@SuppressWarnings({ "unchecked" })
		@Override
		protected boolean matchesSafely(Map map) {
			Objects.requireNonNull(map);
			Iterator<Entry<Object, Object>> iterator = map.entrySet().iterator();
			if (map.size() == entries.size()) {
				for (int i = 0; i < entries.size(); i++) {
					Entry<Object, Object> actual = iterator.next();
					Object[] expected = entries.get(i);
					Object actualKey = actual.getKey();
					Object actualValue = actual.getValue();
					Object expectedKey = expected[0];
					Object expectedValue = expected[1];
					if (!actualKey.equals(expectedKey) || !actualValue.equals(expectedValue)) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
		
	}
	

}

package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

public class DefaultDateTimeProviderTest {
	private static final Clock CONSTANT_CLOCK = Clock.fixed(Instant.now(), ZoneId.systemDefault());
	
	private DateTimeProvider provider = new DefaultDateTimeProvider(CONSTANT_CLOCK);

	@Test
	public void returnsCurrentZonedDateTime() throws Exception {
		ZonedDateTime actual = provider.getCurrentDateTime();
		
		ZonedDateTime expected = ZonedDateTime.now(CONSTANT_CLOCK);
		assertThat(actual, equalTo(expected));
	}

}

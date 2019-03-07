package com.revenat.httpserver.io.impl;

import java.time.Clock;
import java.time.ZonedDateTime;

class DefaultDateTimeProvider implements DateTimeProvider {
	private final Clock systemClock;

	DefaultDateTimeProvider(Clock systemClock) {
		this.systemClock = systemClock;
	}

	@Override
	public ZonedDateTime getCurrentDateTime() {
		return ZonedDateTime.now(systemClock);
	}

}

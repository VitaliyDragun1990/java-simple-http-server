package com.revenat.httpserver.io.impl;

import java.time.ZonedDateTime;

class DefaultDateTimeProvider implements DateTimeProvider {

	@Override
	public ZonedDateTime getCurrentDateTime() {
		return ZonedDateTime.now();
	}

}

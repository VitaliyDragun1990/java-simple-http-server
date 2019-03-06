package com.revenat.httpserver.io.impl;

import static java.util.Objects.requireNonNull;

import com.revenat.httpserver.io.config.HttpServerConfig;

/**
 * Abstract JTTP server component that needs access to the
 * {@link HttpServerConfig} object.
 * 
 * @author Vitaly Dragun
 *
 */
class AbstractHttpConfigurableComponent {
	protected final HttpServerConfig httpServerConfig;

	AbstractHttpConfigurableComponent(HttpServerConfig httpServerConfig) {
		this.httpServerConfig = requireNonNull(httpServerConfig, "HttpServerConfig can not be null");
	}

}

package com.revenat.httpserver.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revenat.httpserver.io.exception.HttpServerConfigException;

class HttpServerClassPathPropertiesLoader implements PropertiesLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerClassPathPropertiesLoader.class);
	private final ClassLoader classLoader;
	
	HttpServerClassPathPropertiesLoader() {
		this.classLoader = HttpServerClassPathPropertiesLoader.class.getClassLoader();
	}

	@Override
	public Properties loadProperties(String resourceName) {
		Properties properties = new Properties();
		try (InputStream in = classLoader.getResourceAsStream(resourceName)) {
			if (in != null) {
				properties.load(in);
				LOGGER.debug("Successful loaded properties from classpath resource: {}", resourceName);
			} else {
				throw new HttpServerConfigException("Classpath resource not found: " + resourceName);
			}
		} catch (IOException e) {
			throw new HttpServerConfigException("Can not load properties from resource: " + resourceName, e);
		}
		return properties;
	}

}

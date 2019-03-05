package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

import com.revenat.httpserver.io.exception.HttpServerConfigException;

public class HttpServerClassPathPropertiesLoaderTest {
	
	private PropertiesLoader loader = new HttpServerClassPathPropertiesLoader();

	@Test
	public void loadsPropertiesFromClassPathResource() throws Exception {
		Properties properties = loader.loadProperties("test.properties");
		
		assertThat(properties.size(), equalTo(2));
		assertThat(properties.getProperty("test"), equalTo("java"));
		assertThat(properties.getProperty("ide"), equalTo("eclipse"));
	}
	
	@Test(expected = HttpServerConfigException.class)
	public void throwsExceptionIfResourceDoesNotExist() throws Exception {
		loader.loadProperties("invalid.properties");
		
	}

}

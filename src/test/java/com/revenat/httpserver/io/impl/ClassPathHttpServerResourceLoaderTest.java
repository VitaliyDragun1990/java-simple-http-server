package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

import com.revenat.httpserver.io.config.HttpServerResourceLoader;
import com.revenat.httpserver.io.exception.HttpServerConfigException;
import com.revenat.httpserver.io.exception.HttpServerException;

public class ClassPathHttpServerResourceLoaderTest {
	
	private HttpServerResourceLoader loader = new ClassPathHttpServerResourceLoader();

	@Test
	public void loadsPropertiesFromClassPathResource() throws Exception {
		Properties properties = loader.loadProperties("test.properties");
		
		assertThat(properties.size(), equalTo(2));
		assertThat(properties.getProperty("test"), equalTo("java"));
		assertThat(properties.getProperty("ide"), equalTo("eclipse"));
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfResourceNameIsNull() throws Exception {
		loader.loadProperties(null);
		
	}
	
	@Test(expected = HttpServerConfigException.class)
	public void throwsExceptionIfResourceDoesNotExist() throws Exception {
		loader.loadProperties("invalid.properties");
	}
	
	@Test
	public void loadsHtmlTemplateFromClassPathLocation() throws Exception {
		String template = loader.loadHtmlTemplate("test.html");
		
		assertThat(template, equalTo("<h1>Hello Java</h1>"));
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfTemplateNameIsNull() throws Exception {
		loader.loadHtmlTemplate(null);
		
	}
	
	@Test(expected = HttpServerException.class)
	public void throwsExceptionIfHtmlTemplateDoesNotExist() throws Exception {
		loader.loadHtmlTemplate("not_found.html");
		
	}

}

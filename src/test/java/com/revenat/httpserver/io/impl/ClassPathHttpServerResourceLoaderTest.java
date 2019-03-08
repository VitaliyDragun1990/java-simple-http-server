package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.revenat.httpserver.io.config.HttpServerResourceLoader;
import com.revenat.httpserver.io.exception.HttpServerConfigException;
import com.revenat.httpserver.io.exception.HttpServerException;

public class ClassPathHttpServerResourceLoaderTest {
	
	@Rule
	public ExpectedException expected = ExpectedException.none();
	
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
	
	@Test
	public void throwsHttpServerExceptionIfIOExceptionDuringPropertiesReading() throws Exception {
		loader = new ClassPathHttpServerResourceLoader() {
			@Override
			protected InputStream getClasspathResource(String resourceName) {
				return Mockito.mock(InputStream.class, (i) -> {throw new IOException();});
			}
		};
		expected.expect(HttpServerConfigException.class);
		expected.expectMessage(containsString("Can not load properties from resource"));
		
		loader.loadProperties("some.properties");
	}
	
	@Test
	public void throwsHttpServerExceptionIfIOExceptionDuringTemplateReading() throws Exception {
		loader = new ClassPathHttpServerResourceLoader() {
			@Override
			protected InputStream getClasspathResource(String resourceName) {
				return Mockito.mock(InputStream.class, (i) -> {throw new IOException();});
			}
		};
		expected.expect(HttpServerException.class);
		expected.expectMessage(containsString("Can not load template"));
		
		loader.loadHtmlTemplate("some_template.html");
	}

}

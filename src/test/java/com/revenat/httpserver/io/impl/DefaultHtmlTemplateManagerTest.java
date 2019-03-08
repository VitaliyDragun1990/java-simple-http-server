package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.revenat.httpserver.io.HtmlTemplateManager;
import com.revenat.httpserver.io.config.HttpServerResourceLoader;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultHtmlTemplateManagerTest {
	private static final String MESSAGE = "MESSAGE";
	private static final String TITLE = "TITLE";
	
	private static final String TEMPLATE = "<h1>${"+TITLE+"}</h1>"
										 + "<p>${"+MESSAGE+"}</p>";

	private static final String TEMPLATE_NAME = "test.html";
	
	@Mock
	private HttpServerResourceLoader resourceLoader;
	
	private HtmlTemplateManager templateManager;
	
	@Before
	public void setup() {
		templateManager = new DefaultHtmlTemplateManager(resourceLoader);
	}

	@Test
	public void processesTemplate() throws Exception {
		String titleVal = "Test title";
		String messageVal = "Test message";
		Map<String, Object> args = getArgs(titleVal, messageVal);
		when(resourceLoader.loadHtmlTemplate(TEMPLATE_NAME)).thenReturn(TEMPLATE);
		
		String actualTemplate = templateManager.processTemplate(TEMPLATE_NAME, args);
		
		String expectedTemplate = getExpectedTemplate(titleVal, messageVal);
		assertThat(actualTemplate, equalTo(expectedTemplate));
	}
	
	@Test
	public void loadsTemplateViaLoaderForTheFirstTimeUsageOfThisTemplate() throws Exception {
		Map<String, Object> args = getArgs("Test title", "Test message");
		when(resourceLoader.loadHtmlTemplate(TEMPLATE_NAME)).thenReturn(TEMPLATE);
		
		templateManager.processTemplate(TEMPLATE_NAME, args);
		
		verify(resourceLoader, times(1)).loadHtmlTemplate(TEMPLATE_NAME);
	}
	
	@Test
	public void cachesTemplateForConsequtiveUsage() throws Exception {
		Map<String, Object> args = getArgs("Test title", "Test message");
		when(resourceLoader.loadHtmlTemplate(TEMPLATE_NAME)).thenReturn(TEMPLATE);
		
		templateManager.processTemplate(TEMPLATE_NAME, args);
		templateManager.processTemplate(TEMPLATE_NAME, args);
		templateManager.processTemplate(TEMPLATE_NAME, args);
		
		verify(resourceLoader, times(1)).loadHtmlTemplate(TEMPLATE_NAME);
		verifyNoMoreInteractions(resourceLoader);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfCallWithNullTemplateName() throws Exception {
		templateManager.processTemplate(null, getArgs("Test title", "Test message"));
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfCallWithNullArgs() throws Exception {
		templateManager.processTemplate(TEMPLATE_NAME, null);
	}

	private String getExpectedTemplate(String titleVal, String messageVal) {
		String templateFormat = "<h1>%s</h1>"
					           + "<p>%s</p>";
		return String.format(templateFormat, titleVal, messageVal);
	}

	private Map<String, Object> getArgs(String titleVal, String messageVal) {
		Map<String, Object> args = new HashMap<>();
		args.put(TITLE, titleVal);
		args.put(MESSAGE, messageVal);
		return args;
	}

}

package com.revenat.httpserver.io.impl;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revenat.httpserver.io.config.HttpServerResourceLoader;
import com.revenat.httpserver.io.exception.HttpServerConfigException;
import com.revenat.httpserver.io.exception.HttpServerException;

/**
 * Default implementation of the {@link HttpServerResourceLoader}
 * that loads required resources from HTTP server's class path.
 * 
 * @author Vitaly Dragun
 *
 */
class ClassPathHttpServerResourceLoader implements HttpServerResourceLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClassPathHttpServerResourceLoader.class);
	private static final String TEMPLATE_DIR = "html/templates/";
	private final ClassLoader classLoader;

	ClassPathHttpServerResourceLoader() {
		this.classLoader = ClassPathHttpServerResourceLoader.class.getClassLoader();
	}

	@Override
	public Properties loadProperties(String resourceName) {
		requireNonNull(resourceName, "Resource name can not be null");
		Properties properties = new Properties();
		try (InputStream in = getClasspathResource(resourceName)) {
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


	@Override
	public String loadHtmlTemplate(String templateName) {
		requireNonNull(templateName, "Template name can not be null");
		String template = null;
			try (InputStream in = getClasspathResource(TEMPLATE_DIR + templateName)) {
				if (in == null) {
					throw new HttpServerException("Classpath resource \"" + TEMPLATE_DIR + templateName + "\" not found");
				}
				template = IOUtils.toString(in, StandardCharsets.UTF_8);
			} catch (IOException e) {
				throw new HttpServerException("Can not load template: " + template, e);
			}
		return template;
	}

	protected InputStream getClasspathResource(String resourceName) {
		return classLoader.getResourceAsStream(resourceName);
	}
}

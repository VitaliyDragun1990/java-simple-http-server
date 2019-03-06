package com.revenat.httpserver.io.config;

import java.util.Properties;

/**
 * Component responsible for loading resources that can be useful
 * in configuring HTTP server.
 * 
 * @author Vitaly Dragun
 *
 */
public interface HttpServerResourceLoader {

	/**
	 * Loads properties from resource with specified {@code resourceName}
	 * 
	 * @param resourceName name of the resource to load properties from.
	 * @return {@link Properties} instance with properties loaded from specified resource
	 */
	Properties loadProperties(String resourceName);
	
	/**
	 * Loads HTML template and returns it as string.
	 * @param templateName name of the HTML template to load
	 * @return HTML template as string.
	 */
	String loadHtmlTemplate(String templateName);
}

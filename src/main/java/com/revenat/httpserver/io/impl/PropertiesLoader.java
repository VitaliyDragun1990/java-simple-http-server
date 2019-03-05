package com.revenat.httpserver.io.impl;

import java.util.Properties;

/**
 * Component responsible for loading properties.
 * 
 * @author Vitaly Dragun
 *
 */
interface PropertiesLoader {

	/**
	 * Loads properties from resource with specified {@code resourceName}
	 * 
	 * @param resourceName name of the resource to load properties from.
	 * @return {@link Properties} instance with properties loaded from specified resource
	 */
	Properties loadProperties(String resourceName);
}

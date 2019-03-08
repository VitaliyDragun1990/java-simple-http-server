package com.revenat.httpserver.io;

import java.util.Map;

/**
 * This component is responsible for managing HTML templates.
 * 
 * @author Vitaly Dragun
 *
 */
public interface HtmlTemplateManager {

	/**
	 * Returns HTML page representation as a string.
	 * 
	 * @param templateName name of the specific HTML page template
	 * @param args         arguments to be inserted into the HTML template
	 * @return processed HTML template as string
	 */
	String processTemplate(String templateName, Map<String, Object> args);
}

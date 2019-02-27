package com.revenat.httpserver.io;

import java.util.Map;

/**
 * This component is responsible for HTML templates processing.
 * @author Vitaly Dragun
 *
 */
public interface HtmlTemplateManager {

	/**
	 * Returns HTML page representation as a string.
	 * @param templateName name of the specific HTML page template
	 * @param args arguments to be processed in the HTML template
	 * @return
	 */
	String pocessTemplate(String templateName, Map<String, Object> args);
}

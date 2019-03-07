package com.revenat.httpserver.io.impl;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;

import com.revenat.httpserver.io.HtmlTemplateManager;
import com.revenat.httpserver.io.config.HttpServerResourceLoader;

/**
 * Reference implementation of the {@link HtmlTemplateManager}
 * 
 * @author Vitaly Dragun
 *
 */
class DefaultHtmlTemplateManager implements HtmlTemplateManager {
	private final Map<String, String> templates = new HashMap<>();
	private final HttpServerResourceLoader resourceLoader;

	DefaultHtmlTemplateManager(HttpServerResourceLoader resourceLoader) {
		this.resourceLoader = requireNonNull(resourceLoader, "resourceLoader can not be null");
	}

	@Override
	public String processTemplate(String templateName, Map<String, Object> args) {
		requireNonNull(templateName, "Template name can not be null");
		requireNonNull(args, "Template args can not be null");
		String template = getTemplate(templateName);
		return populateTemplate(template, args);
	}

	private String getTemplate(String templateName) {
		return templates.computeIfAbsent(templateName, resourceLoader::loadHtmlTemplate);
	}

	private String populateTemplate(String template, Map<String, Object> args) {
		String html = template;
		for (Map.Entry<String, Object> entry : args.entrySet()) {
			html = html.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
		}
		return html;
	}

}

package com.revenat.httpserver.io;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * Component that provides necessary information about HTTP server context (its
 * environment, configuration details, etc.)
 * 
 * @author Vitaly Dragun
 *
 */
public interface HttpServerContext {

	/**
	 * Returns {@link ServerInfo} instance that represents information
	 * about HTTP server's state at the current moment.
	 * @return
	 */
	ServerInfo getServerInfo();
	
	/**
	 * Returns collection with request methods supported by the HTTP server at the moment
	 * @return
	 */
	Collection<String> getSupportedRequestMethods();
	
	/**
	 * Returns {@link Properties} object with supported response statuses.
	 * @return
	 */
	Properties getSupportedResponseStatuses();
	
	/**
	 * Returns {@link DataSource} with connections to the database.
	 * @return
	 */
	DataSource getDataSource();
	
	/**
	 * Returns path to the server root directory
	 * @return
	 */
	Path getRootPath();
	
	/**
	 * Returns appropriate MIME type for specified file extension
	 * @param extension
	 * @return
	 */
	String getContentType(String extension);
	
	/**
	 * Returns {@link HtmlTemplateManager} instance.
	 * @return
	 */
	HtmlTemplateManager getHtmlTemplateManager();
	
	/**
	 * Returns how many days should resource with specified extension to be cached
	 * @param extension extension of the resource
	 * @return
	 */
	Integer getExpiresDaysForResource(String extension);
}

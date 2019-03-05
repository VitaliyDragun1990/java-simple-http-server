package com.revenat.httpserver.io.impl;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.revenat.httpserver.io.Constants;
import com.revenat.httpserver.io.HtmlTemplateManager;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.ServerInfo;
import com.revenat.httpserver.io.exception.HttpServerConfigException;

/**
 * Reference implementation of the {@link HttpServerContext}
 * 
 * @author Vitaly Dragun
 *
 */
class DefaultHttpServerContext extends AbstractHttpConfigurableComponent implements HttpServerContext {

	DefaultHttpServerContext(DefaultHttpServerConfig httpServerConfig) {
		super(httpServerConfig);
	}

	private DefaultHttpServerConfig getHttpServerConfig() {
		return (DefaultHttpServerConfig) httpServerConfig;
	}

	@Override
	public ServerInfo getServerInfo() {
		return getHttpServerConfig().getServerInfo();
	}

	@Override
	public Collection<String> getSupportedRequestMethods() {
		return Constants.ALLOWED_METHODS;
	}

	@Override
	public Properties getSupportedResponseStatuses() {
		Properties props = new Properties();
		props.putAll(getHttpServerConfig().getStatusesProperties());
		return props;
	}

	@Override
	public DataSource getDataSource() {
		BasicDataSource dataSource = getHttpServerConfig().getDataSource();
		if (dataSource != null) {
			return dataSource;
		} else {
			throw new HttpServerConfigException("Datasource is not configured for this context");
		}
	}

	@Override
	public Path getRootPath() {
		return getHttpServerConfig().getRootPath();
	}

	@Override
	public String getContentType(String extension) {
		String result = getHttpServerConfig().getMimeTypesProperties().getProperty(extension);
		return result != null ? result : "text/plain";
	}

	@Override
	public HtmlTemplateManager getHtmlTemplateManager() {
		return getHttpServerConfig().getHtmlTemplateManager();
	}

	@Override
	public Integer getExpiresDaysForResource(String extension) {
		if (getHttpServerConfig().getStaticExpiresExtensions().contains(extension)) {
			return getHttpServerConfig().getStaticExpiresDays();
		}
		return null;
	}

}

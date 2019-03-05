package com.revenat.httpserver.io.impl;

import static java.lang.Integer.parseInt;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revenat.httpserver.io.HtmlTemplateManager;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.ServerInfo;
import com.revenat.httpserver.io.config.HttpClientSocketHandler;
import com.revenat.httpserver.io.config.HttpRequestDispatcher;
import com.revenat.httpserver.io.config.HttpRequestParser;
import com.revenat.httpserver.io.config.HttpResponseBuilder;
import com.revenat.httpserver.io.config.HttpResponseWriter;
import com.revenat.httpserver.io.config.HttpServerConfig;
import com.revenat.httpserver.io.exception.HttpServerConfigException;

/**
 * Reference implementation of the {@link HttpServerConfig}
 * 
 * @author Vitaly Dragun
 *
 */
class DefaultHttpServerConfig implements HttpServerConfig {
	private static final String WORKER_THREAD_PREFIX = "executor-thread-";

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHttpServerConfig.class);

	/**
	 * Configurable properties for flexible server configuration.
	 */
	private final Properties serverProperties = new Properties();
	private final Properties statusesProperties = new Properties();
	private final Properties mimeTypesProperties = new Properties();

	/**
	 * HTTP server external resources: data source with connections to the server's
	 * database and path to the server's root directory in the file system
	 */
	private final BasicDataSource dataSource;
	private final Path rootPath;

	/**
	 * Main HTTP server components that exist in a single instance for the whole
	 * application.
	 */
	private final HttpServerContext httpServerContext;
	private final HttpRequestParser httpRequestParser;
	private final HttpResponseBuilder httpResponseBuilder;
	private final HttpResponseWriter httpResponseWriter;
	private final HttpRequestDispatcher httpRequestDispatcher;
	private final ThreadFactory workerThreadFactory;
	private final HtmlTemplateManager htmlTemplateManager;
	private final ServerInfo serverInfo;
	
	/**
	 * List of extensions for static resources to which
	 * certain expires period can be applied.
	 */
	private final List<String> staticExpiresExtensions;
	private final int staticExpiresDays;
	
	DefaultHttpServerConfig(Properties overrideServerProperties, PropertiesLoader propertiesLoader) {
		loadAllProperties(overrideServerProperties, propertiesLoader);
		
		this.rootPath = createRootPath();
		this.dataSource = createBasicDataSource();
		this.serverInfo = createServerInfo();
		this.staticExpiresDays = parseInt(this.serverProperties.getProperty("webapp.static.expires.days"));
		this.staticExpiresExtensions = Arrays.asList(
				this.serverProperties.getProperty("webapp.static.expires.extensions").split(","));
		
		// Create default implementations
		this.httpServerContext = new DefaultHttpServerContext(this);
		this.httpRequestParser = new DefaultHttpRequestParser();
		this.httpResponseWriter = new DefaultHttpResponseWriter(this);
		this.httpResponseBuilder = new DefaultHttpResponseBuilder(this, new DefaultDateTimeProvider());
//		this.httpRequestDispatcher = new HelloWorldHttpRequestDispatcher();
		this.httpRequestDispatcher = new SimpleHttpRequestDispatcher(new StaticResourcesGetHttpHandler());
		this.workerThreadFactory = new DefaultThreadFactory(WORKER_THREAD_PREFIX);
		this.htmlTemplateManager = null;
	}

	protected void loadAllProperties(Properties overrideServerProperties, PropertiesLoader propertiesLoader) {
		serverProperties.putAll(propertiesLoader.loadProperties("server.properties"));
		statusesProperties.putAll(propertiesLoader.loadProperties("statuses.properties"));
		mimeTypesProperties.putAll(propertiesLoader.loadProperties("mime-types.properties"));
		
		if (overrideServerProperties != null) {
			LOGGER.info("Overrides default server properties");
			this.serverProperties.putAll(overrideServerProperties);
		}
		
		logServerProperties();
	}

	protected void logServerProperties() {
		if (LOGGER.isDebugEnabled()) {
			StringBuilder log = new StringBuilder("Current server properties is:\n");
			for (Map.Entry<Object, Object> entry : this.serverProperties.entrySet()) {
				log.append(String.format("%s=%s%n", entry.getKey(), entry.getValue()));
			}
			LOGGER.debug(log.toString());
		}
	}
	
	protected Path createRootPath() {
		Path path = Paths.get(new File(
				this.serverProperties.getProperty("webapp.static.dir.root")).getAbsoluteFile().toURI());
		
		if (!path.toFile().exists()) {
			throw new HttpServerConfigException("Root path not found: " + path.toString());
		}
		if (!path.toFile().isDirectory()) {
			throw new HttpServerConfigException("Root path is not a directory: " + path.toString());
		}
		
		LOGGER.info("Root path is {}", path.toAbsolutePath());
		return path;
	}
	
	protected BasicDataSource createBasicDataSource() {
		BasicDataSource ds = null;
		if (Boolean.parseBoolean(serverProperties.getProperty("db.datasource.enabled"))) {
			ds = new BasicDataSource();
			
			ds.setDefaultAutoCommit(false);
			ds.setRollbackOnReturn(true);
			ds.setDriverClassName(requireNonNull(serverProperties.getProperty("db.datasource.driver")));
			ds.setUrl(requireNonNull(serverProperties.getProperty("db.datasource.url")));
			ds.setUsername(requireNonNull(serverProperties.getProperty("db.datasource.username")));
			ds.setPassword(requireNonNull(serverProperties.getProperty("db.datasource.password")));
			ds.setInitialSize(parseInt(requireNonNull(serverProperties.getProperty("db.datasource.pool.initSize"))));
			ds.setMaxTotal(parseInt(requireNonNull(serverProperties.getProperty("db.datasource.pool.maxSize"))));
			
			LOGGER.info("Datasource is enabled. JDBC url is {}", ds.getUrl());
		} else {
			LOGGER.info("Datasource is disabled");
		}
		
		return ds;
	}
	
	protected ServerInfo createServerInfo() {
		ServerInfo info = new ServerInfo(
				serverProperties.getProperty("server.name"),
				parseInt(serverProperties.getProperty("server.port")),
				parseInt(serverProperties.getProperty("server.thread.count")));
		
		if (info.getThreadCount() < 0) {
			throw new HttpServerConfigException("server.thread.count should be >= 0");
		}
		
		return info;
	}

	@Override
	public ServerInfo getServerInfo() {
		return serverInfo;
	}

	@Override
	public String getStatusMessage(int statusCode) {
		String message = statusesProperties.getProperty(String.valueOf(statusCode));
		return message != null ? message : statusesProperties.getProperty("500");
	}

	@Override
	public HttpRequestParser getHttpRequestParser() {
		return httpRequestParser;
	}

	@Override
	public HttpResponseBuilder getHttpResponseBuilder() {
		return httpResponseBuilder;
	}

	@Override
	public HttpResponseWriter getHttpResponseWriter() {
		return httpResponseWriter;
	}

	@Override
	public HttpServerContext getHttpServerContext() {
		return httpServerContext;
	}

	@Override
	public HttpRequestDispatcher getHttpRequestDispatcher() {
		return httpRequestDispatcher;
	}

	@Override
	public ThreadFactory getWorkerThreadFactory() {
		return workerThreadFactory;
	}

	@Override
	public HttpClientSocketHandler buildNewHttpClientSocketHandler(Socket clientSocket) {
		return new DefaultHttpClientSocketHandler(clientSocket, this);
	}

	@Override
	public void close() throws Exception {
		if (dataSource != null) {
			try {
				dataSource.close();
			} catch (SQLException e) {
				LOGGER.error("Close datasource failed: " + e.getMessage(), e);
			}
		}
		LOGGER.info("DefaultHttpServerConfig closed");
	}
	
	protected Properties getServerProperties() {
		return serverProperties;
	}
	
	protected Properties getStatusesProperties() {
		return statusesProperties;
	}
	
	protected Properties getMimeTypesProperties() {
		return mimeTypesProperties;
	}
	
	protected BasicDataSource getDataSource() {
		return dataSource;
	}
	
	protected Path getRootPath() {
		return rootPath;
	}

	// TODO: Consider adding getHtmlTemplateManager() method to public interface of HttpServerConfig
	protected HtmlTemplateManager getHtmlTemplateManager() {
		return htmlTemplateManager;
	}

	protected List<String> getStaticExpiresExtensions() {
		return staticExpiresExtensions;
	}

	protected int getStaticExpiresDays() {
		return staticExpiresDays;
	}
	
}

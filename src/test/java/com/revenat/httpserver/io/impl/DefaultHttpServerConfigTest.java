package com.revenat.httpserver.io.impl;

import static com.revenat.httpserver.io.impl.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.ServerInfo;
import com.revenat.httpserver.io.config.HttpClientSocketHandler;
import com.revenat.httpserver.io.config.HttpRequestDispatcher;
import com.revenat.httpserver.io.config.HttpRequestParser;
import com.revenat.httpserver.io.config.HttpResponseBuilder;
import com.revenat.httpserver.io.config.HttpResponseWriter;
import com.revenat.httpserver.io.exception.HttpServerConfigException;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultHttpServerConfigTest {

	private static final String ROOT_PATH = TestUtils.ROOT_PATH;
	private static final String STATUSES_PROPS_RESOURCE = TestUtils.STATUSES_PROPS_RESOURCE;
	private static final String SERVER_PROPS_RESOURCE = TestUtils.SERVER_PROPS_RESOURCE;
	private static final String MIME_PROPS_RESOURCE = TestUtils.MIME_PROPS_RESOURCE;
	private final Properties MIME_PROPERTIES = createMimeProperties();
	private final Properties STATUSES_PROPERTIES = createStatusesProperties();
	private final Properties SERVER_PROPERTIES = createServerProperties();

	@Mock
	private PropertiesLoader propertiesLoader;

	private DefaultHttpServerConfig serverConfig;

	private void setupTestProperties() {
		when(propertiesLoader.loadProperties(STATUSES_PROPS_RESOURCE)).thenReturn(STATUSES_PROPERTIES);
		when(propertiesLoader.loadProperties(MIME_PROPS_RESOURCE)).thenReturn(MIME_PROPERTIES);
		when(propertiesLoader.loadProperties(SERVER_PROPS_RESOURCE)).thenReturn(SERVER_PROPERTIES);
	}

	@Test
	public void createsHttpServerConfigWithAllLoadedProperties() throws IOException {
		setupTestProperties();

		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);

		assertThat(serverConfig.getMimeTypesProperties(), equalTo(MIME_PROPERTIES));
		assertThat(serverConfig.getServerProperties(), equalTo(SERVER_PROPERTIES));
		assertThat(serverConfig.getStatusesProperties(), equalTo(STATUSES_PROPERTIES));
	}

	@Test(expected = HttpServerConfigException.class)
	public void throwsExceptionIfCanNotLoadServerProperties() throws Exception {
		setupTestProperties();
		when(propertiesLoader.loadProperties(SERVER_PROPS_RESOURCE))
				.thenThrow(new HttpServerConfigException("Can not load props"));

		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
	}

	@Test(expected = HttpServerConfigException.class)
	public void throwsExceptionIfCanNotLoadStatusesProperties() throws Exception {
		setupTestProperties();
		when(propertiesLoader.loadProperties(STATUSES_PROPS_RESOURCE))
				.thenThrow(new HttpServerConfigException("Can not load props"));

		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
	}

	@Test(expected = HttpServerConfigException.class)
	public void throwsExceptionIfCanNotLoadMimeProperties() throws Exception {
		setupTestProperties();
		when(propertiesLoader.loadProperties(MIME_PROPS_RESOURCE))
				.thenThrow(new HttpServerConfigException("Can not load props"));

		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
	}

	@Test
	public void overridesServerPropsIfSpecifiedWhenCreating() throws Exception {
		setupTestProperties();
		Properties overrideProps = new Properties();
		overrideProps.setProperty("server.port", "90");

		serverConfig = new DefaultHttpServerConfig(overrideProps, propertiesLoader);

		assertThat(serverConfig.getServerProperties().get("server.port"), equalTo("90"));
	}

	@Test
	public void returnsStaticExpiresDaysAsSpecifiedInServerProperties() throws Exception {
		setupTestProperties();

		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);

		int actual = serverConfig.getStaticExpiresDays();
		int expected = Integer.parseInt(SERVER_PROPERTIES.getProperty("webapp.static.expires.days"));
		assertThat(actual, equalTo(expected));

	}

	@Test
	public void returnsStaticExpiresExtensionsAsSpecifiedInServerProperties() throws Exception {
		setupTestProperties();

		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);

		List<String> actual = serverConfig.getStaticExpiresExtensions();

		List<String> expected = Arrays
				.asList(SERVER_PROPERTIES.getProperty("webapp.static.expires.extensions").split(","));
		assertThat(actual, equalTo(expected));
	}
	
	@Test(expected = HttpServerConfigException.class)
	public void throwsExceptionIfRootPathPropertyInvalid() throws Exception {
		setupTestProperties();
		SERVER_PROPERTIES.setProperty("webapp.static.dir.root", "/wrong/dir");
		
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsNullPointerExceptionIfMissDatasourceDriverProperty() throws Exception {
		setupTestProperties();
		SERVER_PROPERTIES.remove("db.datasource.driver");
		
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsNullPointerExceptionIfMissDatasourceUrlProperty() throws Exception {
		setupTestProperties();
		SERVER_PROPERTIES.remove("db.datasource.url");
		
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsNullPointerExceptionIfMissDatasourceUsernameProperty() throws Exception {
		setupTestProperties();
		SERVER_PROPERTIES.remove("db.datasource.username");
		
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsNullPointerExceptionIfMissDatasourcePasswordProperty() throws Exception {
		setupTestProperties();
		SERVER_PROPERTIES.remove("db.datasource.password");
		
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsNullPointerExceptionIfMissDatasourcePoolInitSizeProperty() throws Exception {
		setupTestProperties();
		SERVER_PROPERTIES.remove("db.datasource.pool.initSize");
		
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsNullPointerExceptionIfMissDatasourcePoolMaxSizeProperty() throws Exception {
		setupTestProperties();
		SERVER_PROPERTIES.remove("db.datasource.pool.maxSize");
		
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
	}
	
	@Test
	public void returnsCorrectStatusMessageForSupportedStatusCode() throws Exception {
		setupTestProperties();
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
		
		String statusMessage = serverConfig.getStatusMessage(200);
		assertThat(statusMessage, equalTo("OK"));
	}
	
	@Test
	public void returnsStatusMessageforCode500ForAnyUnsupportedStatusCode() throws Exception {
		setupTestProperties();
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
		
		String statusMessage = serverConfig.getStatusMessage(350);
		assertThat(statusMessage, equalTo("Internal Server Error"));
		
	}
	
	@Test
	public void returnsCorrectRootPath() throws Exception {
		setupTestProperties();
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
		
		assertThat(serverConfig.getRootPath(), equalTo(Paths.get(new File(ROOT_PATH).getAbsoluteFile().toURI())));
	}
	
	@Test
	public void returnsTheSameInstanceOfServerInfoForEveryCall() throws Exception {
		setupTestProperties();
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
		
		ServerInfo instanceA = serverConfig.getServerInfo();
		ServerInfo instanceB = serverConfig.getServerInfo();
		
		assertThat(instanceA, sameInstance(instanceB));
	}
	
	@Test
	public void returnsTheSameInstanceOfHttpRequestParserForEveryCall() throws Exception {
		setupTestProperties();
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
		
		HttpRequestParser instanceA = serverConfig.getHttpRequestParser();
		HttpRequestParser instanceB = serverConfig.getHttpRequestParser();
		
		assertThat(instanceA, sameInstance(instanceB));
	}
	
	@Test
	public void returnsTheSameInstanceOfHttpResponseBuilderForEveryCall() throws Exception {
		setupTestProperties();
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
		
		HttpResponseBuilder instanceA = serverConfig.getHttpResponseBuilder();
		HttpResponseBuilder instanceB = serverConfig.getHttpResponseBuilder();
		
		assertThat(instanceA, sameInstance(instanceB));
	}
	
	@Test
	public void returnsTheSameInstanceOfHttpResponseWriterForEveryCall() throws Exception {
		setupTestProperties();
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
		
		HttpResponseWriter instanceA = serverConfig.getHttpResponseWriter();
		HttpResponseWriter instanceB = serverConfig.getHttpResponseWriter();
		
		assertThat(instanceA, sameInstance(instanceB));
	}
	
	@Test
	public void returnsTheSameInstanceOfHttpServerContextForEveryCall() throws Exception {
		setupTestProperties();
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
		
		HttpServerContext instanceA = serverConfig.getHttpServerContext();
		HttpServerContext instanceB = serverConfig.getHttpServerContext();
		
		assertThat(instanceA, sameInstance(instanceB));
	}
	
	@Test
	public void returnsTheSameInstanceOfHttpRequestDispatcherForEveryCall() throws Exception {
		setupTestProperties();
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
		
		HttpRequestDispatcher instanceA = serverConfig.getHttpRequestDispatcher();
		HttpRequestDispatcher instanceB = serverConfig.getHttpRequestDispatcher();
		
		assertThat(instanceA, sameInstance(instanceB));
	}

	@Test
	public void returnsTheSameInstanceOfThreadFactoryForEveryCall() throws Exception {
		setupTestProperties();
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
		
		ThreadFactory instanceA = serverConfig.getWorkerThreadFactory();
		ThreadFactory instanceB = serverConfig.getWorkerThreadFactory();
		
		assertThat(instanceA, sameInstance(instanceB));
	}
	
	@Test
	public void returnsNewInstanceOfHttpClientSocketHandlerForEveryCall() throws Exception {
		setupTestProperties();
		serverConfig = new DefaultHttpServerConfig(null, propertiesLoader);
		Socket clientSocket = new Socket();
		
		HttpClientSocketHandler instanceA = serverConfig.buildNewHttpClientSocketHandler(clientSocket);
		HttpClientSocketHandler instanceB = serverConfig.buildNewHttpClientSocketHandler(clientSocket);
		
		assertThat(instanceA, not(sameInstance(instanceB)));
	}

}

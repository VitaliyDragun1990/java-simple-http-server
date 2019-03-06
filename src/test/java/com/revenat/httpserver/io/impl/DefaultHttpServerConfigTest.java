package com.revenat.httpserver.io.impl;

import static com.revenat.httpserver.io.impl.TestUtils.createMimeProperties;
import static com.revenat.httpserver.io.impl.TestUtils.createServerProperties;
import static com.revenat.httpserver.io.impl.TestUtils.createStatusesProperties;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.revenat.httpserver.io.HttpHandlerRegistrar;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.ServerInfo;
import com.revenat.httpserver.io.config.HttpClientSocketHandler;
import com.revenat.httpserver.io.config.HttpRequestDispatcher;
import com.revenat.httpserver.io.config.HttpRequestParser;
import com.revenat.httpserver.io.config.HttpResponseBuilder;
import com.revenat.httpserver.io.config.HttpResponseWriter;
import com.revenat.httpserver.io.config.HttpServerResourceLoader;
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
	private HttpServerResourceLoader resourceLoader;

	private DefaultHttpServerConfig serverConfig;

	private void setupTestProperties() {
		when(resourceLoader.loadProperties(STATUSES_PROPS_RESOURCE)).thenReturn(STATUSES_PROPERTIES);
		when(resourceLoader.loadProperties(MIME_PROPS_RESOURCE)).thenReturn(MIME_PROPERTIES);
		when(resourceLoader.loadProperties(SERVER_PROPS_RESOURCE)).thenReturn(SERVER_PROPERTIES);
	}

	@Test
	public void createsHttpServerConfigWithAllLoadedProperties() throws IOException {
		setupTestProperties();

		serverConfig = createServerConfig(null, resourceLoader);

		assertThat(serverConfig.getMimeTypesProperties(), equalTo(MIME_PROPERTIES));
		assertThat(serverConfig.getServerProperties(), equalTo(SERVER_PROPERTIES));
		assertThat(serverConfig.getStatusesProperties(), equalTo(STATUSES_PROPERTIES));
	}

	private DefaultHttpServerConfig createServerConfig(Properties overrideServerProperties,
			HttpServerResourceLoader resourceLoader) {
		return new DefaultHttpServerConfig(new HttpHandlerRegistrar(), overrideServerProperties,
				resourceLoader);
	}

	@Test(expected = HttpServerConfigException.class)
	public void throwsExceptionIfCanNotLoadServerProperties() throws Exception {
		setupTestProperties();
		when(resourceLoader.loadProperties(SERVER_PROPS_RESOURCE))
				.thenThrow(new HttpServerConfigException("Can not load props"));

		serverConfig = createServerConfig(null, resourceLoader);
	}

	@Test(expected = HttpServerConfigException.class)
	public void throwsExceptionIfCanNotLoadStatusesProperties() throws Exception {
		setupTestProperties();
		when(resourceLoader.loadProperties(STATUSES_PROPS_RESOURCE))
				.thenThrow(new HttpServerConfigException("Can not load props"));

		serverConfig = createServerConfig(null, resourceLoader);
	}

	@Test(expected = HttpServerConfigException.class)
	public void throwsExceptionIfCanNotLoadMimeProperties() throws Exception {
		setupTestProperties();
		when(resourceLoader.loadProperties(MIME_PROPS_RESOURCE))
				.thenThrow(new HttpServerConfigException("Can not load props"));

		serverConfig = createServerConfig(null, resourceLoader);
	}

	@Test
	public void overridesServerPropsIfSpecifiedWhenCreating() throws Exception {
		setupTestProperties();
		Properties overrideProps = new Properties();
		overrideProps.setProperty("server.port", "90");

		serverConfig = createServerConfig(overrideProps, resourceLoader);

		assertThat(serverConfig.getServerProperties().get("server.port"), equalTo("90"));
	}

	@Test
	public void returnsStaticExpiresDaysAsSpecifiedInServerProperties() throws Exception {
		setupTestProperties();

		serverConfig = createServerConfig(null, resourceLoader);

		int actual = serverConfig.getStaticExpiresDays();
		int expected = Integer.parseInt(SERVER_PROPERTIES.getProperty("webapp.static.expires.days"));
		assertThat(actual, equalTo(expected));

	}

	@Test
	public void returnsStaticExpiresExtensionsAsSpecifiedInServerProperties() throws Exception {
		setupTestProperties();

		serverConfig = createServerConfig(null, resourceLoader);

		List<String> actual = serverConfig.getStaticExpiresExtensions();

		List<String> expected = Arrays
				.asList(SERVER_PROPERTIES.getProperty("webapp.static.expires.extensions").split(","));
		assertThat(actual, equalTo(expected));
	}
	
	@Test(expected = HttpServerConfigException.class)
	public void throwsExceptionIfRootPathPropertyInvalid() throws Exception {
		setupTestProperties();
		SERVER_PROPERTIES.setProperty("webapp.static.dir.root", "/wrong/dir");
		
		serverConfig = createServerConfig(null, resourceLoader);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsNullPointerExceptionIfMissDatasourceDriverProperty() throws Exception {
		setupTestProperties();
		SERVER_PROPERTIES.remove("db.datasource.driver");
		
		serverConfig = createServerConfig(null, resourceLoader);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsNullPointerExceptionIfMissDatasourceUrlProperty() throws Exception {
		setupTestProperties();
		SERVER_PROPERTIES.remove("db.datasource.url");
		
		serverConfig = createServerConfig(null, resourceLoader);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsNullPointerExceptionIfMissDatasourceUsernameProperty() throws Exception {
		setupTestProperties();
		SERVER_PROPERTIES.remove("db.datasource.username");
		
		serverConfig = createServerConfig(null, resourceLoader);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsNullPointerExceptionIfMissDatasourcePasswordProperty() throws Exception {
		setupTestProperties();
		SERVER_PROPERTIES.remove("db.datasource.password");
		
		serverConfig = createServerConfig(null, resourceLoader);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsNullPointerExceptionIfMissDatasourcePoolInitSizeProperty() throws Exception {
		setupTestProperties();
		SERVER_PROPERTIES.remove("db.datasource.pool.initSize");
		
		serverConfig = createServerConfig(null, resourceLoader);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsNullPointerExceptionIfMissDatasourcePoolMaxSizeProperty() throws Exception {
		setupTestProperties();
		SERVER_PROPERTIES.remove("db.datasource.pool.maxSize");
		
		serverConfig = createServerConfig(null, resourceLoader);
	}
	
	@Test
	public void returnsCorrectStatusMessageForSupportedStatusCode() throws Exception {
		setupTestProperties();
		serverConfig = createServerConfig(null, resourceLoader);
		
		String statusMessage = serverConfig.getStatusMessage(200);
		assertThat(statusMessage, equalTo("OK"));
	}
	
	@Test
	public void returnsStatusMessageforCode500ForAnyUnsupportedStatusCode() throws Exception {
		setupTestProperties();
		serverConfig = createServerConfig(null, resourceLoader);
		
		String statusMessage = serverConfig.getStatusMessage(350);
		assertThat(statusMessage, equalTo("Internal Server Error"));
		
	}
	
	@Test
	public void returnsCorrectRootPath() throws Exception {
		setupTestProperties();
		serverConfig = createServerConfig(null, resourceLoader);
		
		assertThat(serverConfig.getRootPath(), equalTo(Paths.get(new File(ROOT_PATH).getAbsoluteFile().toURI())));
	}
	
	@Test
	public void returnsTheSameInstanceOfServerInfoForEveryCall() throws Exception {
		setupTestProperties();
		serverConfig = createServerConfig(null, resourceLoader);
		
		ServerInfo instanceA = serverConfig.getServerInfo();
		ServerInfo instanceB = serverConfig.getServerInfo();
		
		assertThat(instanceA, sameInstance(instanceB));
	}
	
	@Test
	public void returnsTheSameInstanceOfHttpRequestParserForEveryCall() throws Exception {
		setupTestProperties();
		serverConfig = createServerConfig(null, resourceLoader);
		
		HttpRequestParser instanceA = serverConfig.getHttpRequestParser();
		HttpRequestParser instanceB = serverConfig.getHttpRequestParser();
		
		assertThat(instanceA, sameInstance(instanceB));
	}
	
	@Test
	public void returnsTheSameInstanceOfHttpResponseBuilderForEveryCall() throws Exception {
		setupTestProperties();
		serverConfig = createServerConfig(null, resourceLoader);
		
		HttpResponseBuilder instanceA = serverConfig.getHttpResponseBuilder();
		HttpResponseBuilder instanceB = serverConfig.getHttpResponseBuilder();
		
		assertThat(instanceA, sameInstance(instanceB));
	}
	
	@Test
	public void returnsTheSameInstanceOfHttpResponseWriterForEveryCall() throws Exception {
		setupTestProperties();
		serverConfig = createServerConfig(null, resourceLoader);
		
		HttpResponseWriter instanceA = serverConfig.getHttpResponseWriter();
		HttpResponseWriter instanceB = serverConfig.getHttpResponseWriter();
		
		assertThat(instanceA, sameInstance(instanceB));
	}
	
	@Test
	public void returnsTheSameInstanceOfHttpServerContextForEveryCall() throws Exception {
		setupTestProperties();
		serverConfig = createServerConfig(null, resourceLoader);
		
		HttpServerContext instanceA = serverConfig.getHttpServerContext();
		HttpServerContext instanceB = serverConfig.getHttpServerContext();
		
		assertThat(instanceA, sameInstance(instanceB));
	}
	
	@Test
	public void returnsTheSameInstanceOfHttpRequestDispatcherForEveryCall() throws Exception {
		setupTestProperties();
		serverConfig = createServerConfig(null, resourceLoader);
		
		HttpRequestDispatcher instanceA = serverConfig.getHttpRequestDispatcher();
		HttpRequestDispatcher instanceB = serverConfig.getHttpRequestDispatcher();
		
		assertThat(instanceA, sameInstance(instanceB));
	}

	@Test
	public void returnsTheSameInstanceOfThreadFactoryForEveryCall() throws Exception {
		setupTestProperties();
		serverConfig = createServerConfig(null, resourceLoader);
		
		ThreadFactory instanceA = serverConfig.getWorkerThreadFactory();
		ThreadFactory instanceB = serverConfig.getWorkerThreadFactory();
		
		assertThat(instanceA, sameInstance(instanceB));
	}
	
	@Test
	public void returnsNewInstanceOfHttpClientSocketHandlerForEveryCall() throws Exception {
		setupTestProperties();
		serverConfig = createServerConfig(null, resourceLoader);
		Socket clientSocket = new StubSocket();
		
		HttpClientSocketHandler instanceA = serverConfig.buildNewHttpClientSocketHandler(clientSocket);
		HttpClientSocketHandler instanceB = serverConfig.buildNewHttpClientSocketHandler(clientSocket);
		
		assertThat(instanceA, not(sameInstance(instanceB)));
	}
	
	private static class StubSocket extends Socket {
		@Override
		public SocketAddress getRemoteSocketAddress() {
			return new InetSocketAddress("localhost", 888);
		}
	}

}

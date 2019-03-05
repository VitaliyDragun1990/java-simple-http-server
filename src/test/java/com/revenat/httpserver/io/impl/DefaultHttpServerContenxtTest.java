package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.revenat.httpserver.io.Constants;
import com.revenat.httpserver.io.HtmlTemplateManager;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.ServerInfo;
import com.revenat.httpserver.io.exception.HttpServerConfigException;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultHttpServerContenxtTest {
	private static final BasicDataSource DUMMY_DATA_SOURCE = new BasicDataSource();

	@Mock
	private DefaultHttpServerConfig config;
	@Mock
	private ServerInfo serverInfo;
	@Mock
	private HtmlTemplateManager templateManager;

	private HttpServerContext context;

	@Before
	public void setup() {
		context = new DefaultHttpServerContext(config);
	}

	@Test
	public void returnsServerInfo() throws Exception {
		when(config.getServerInfo()).thenReturn(serverInfo);

		ServerInfo info = context.getServerInfo();

		verify(config, times(1)).getServerInfo();
		assertThat(info, equalTo(serverInfo));
	}

	@Test
	public void returnsAllSupportedRequestMethods() throws Exception {
		Collection<String> supportedRequestMethods = context.getSupportedRequestMethods();

		assertThat(supportedRequestMethods, equalTo(Constants.ALLOWED_METHODS));
	}

	@Test
	public void returnsSupportedResponseStatuses() throws Exception {
		Properties statuses = new Properties();
		statuses.setProperty("200", "OK");
		when(config.getStatusesProperties()).thenReturn(statuses);

		Properties responseStatuses = context.getSupportedResponseStatuses();

		assertThat(responseStatuses, equalTo(statuses));
		verify(config, times(1)).getStatusesProperties();
	}

	@Test
	public void returnsConfiguredDataSource() throws Exception {
		when(config.getDataSource()).thenReturn(DUMMY_DATA_SOURCE);
		
		DataSource dataSource = context.getDataSource();
		
		assertThat(dataSource, equalTo(DUMMY_DATA_SOURCE));
		verify(config, times(1)).getDataSource();
	}
	
	@Test(expected = HttpServerConfigException.class)
	public void throwsExceptionIfNoConfiguredDataSource() throws Exception {
		when(config.getDataSource()).thenReturn(null);
		
		context.getDataSource();
		
	}
	
	@Test
	public void returnsRootPath() throws Exception {
		Path root = Paths.get("");
		when(config.getRootPath()).thenReturn(root);
		
		Path rootPath = context.getRootPath();
		
		assertThat(rootPath, equalTo(root));
		verify(config, times(1)).getRootPath();
	}
	
	@Test
	public void returnsContentType() throws Exception {
		Properties mimeTypes = new Properties();
		mimeTypes.setProperty("exe", "application/executable");
		when(config.getMimeTypesProperties()).thenReturn(mimeTypes);
		
		String contentType = context.getContentType("exe");
		assertThat(contentType, equalTo("application/executable"));
		verify(config, times(1)).getMimeTypesProperties();
	}
	
	@Test
	public void returnsTextPlainContentTypeForUnknownExtension() throws Exception {
		Properties mimeTypes = new Properties();
		mimeTypes.setProperty("exe", "application/executable");
		when(config.getMimeTypesProperties()).thenReturn(mimeTypes);
		
		String contentType = context.getContentType("ini");
		assertThat(contentType, equalTo("text/plain"));
		verify(config, times(1)).getMimeTypesProperties();
		
	}
	
	@Test
	public void returnsHtmlTemplateManager() throws Exception {
		when(config.getHtmlTemplateManager()).thenReturn(templateManager);
		
		HtmlTemplateManager htmlTemplateManager = context.getHtmlTemplateManager();
		
		assertThat(htmlTemplateManager, equalTo(templateManager));
		verify(config, times(1)).getHtmlTemplateManager();
	}
	
	@Test
	public void returnsExpiresDaysForKnownStaticResource() throws Exception {
		List<String> staticExpiresExtensions = Arrays.asList("js");
		int defaultDays = 7;
		when(config.getStaticExpiresExtensions()).thenReturn(staticExpiresExtensions);
		when(config.getStaticExpiresDays()).thenReturn(defaultDays);
		
		Integer days = context.getExpiresDaysForResource("js");
		assertThat(days, equalTo(defaultDays));
		verify(config, times(1)).getStaticExpiresExtensions();
		verify(config, times(1)).getStaticExpiresDays();
	}
	
	@Test
	public void returnsNullAsExpiresDaysForUnknownStaticResource() throws Exception {
		List<String> staticExpiresExtensions = Arrays.asList("js");
		int defaultDays = 7;
		when(config.getStaticExpiresExtensions()).thenReturn(staticExpiresExtensions);
		when(config.getStaticExpiresDays()).thenReturn(defaultDays);
		
		Integer days = context.getExpiresDaysForResource("css");
		assertThat(days, nullValue());
		verify(config, times(1)).getStaticExpiresExtensions();
	}

}

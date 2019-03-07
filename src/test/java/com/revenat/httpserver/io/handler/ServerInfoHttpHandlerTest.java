package com.revenat.httpserver.io.handler;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.revenat.httpserver.io.HtmlTemplateManager;
import com.revenat.httpserver.io.HttpHandler;
import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.HttpResponse;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.ServerInfo;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ServerInfoHttpHandlerTest {
	private static final String SERVER_NAME = "test server";
	private static final int SERVER_PORT = 9090;
	private static final int SERVER_THREAD_COUNT = 10;
	private static final Collection<String> SUPPORTED_METHODS = Arrays.asList("GET", "POST");
	private static final Properties SUPPORTED_STATUSES = defineStatuses(new String[]{"200", "OK"},
																		new String[]{"400", "Bad Request"});
	
	@Mock
	private HttpServerContext context;
	@Mock
	private HtmlTemplateManager templateManager;
	
	private ServerInfo serverInfo;
	
	private HttpHandler handler;
	
	@Before
	public void setup() {
		serverInfo = createServerInfo(SERVER_NAME, SERVER_PORT, SERVER_THREAD_COUNT);
		when(context.getHtmlTemplateManager()).thenReturn(templateManager);
		when(context.getServerInfo()).thenReturn(serverInfo);
		when(context.getSupportedRequestMethods()).thenReturn(SUPPORTED_METHODS);
		when(context.getSupportedResponseStatuses()).thenReturn(SUPPORTED_STATUSES);
		handler = new ServerInfoHttpHandler();
	}

	private static Properties defineStatuses(String[]... statuses) {
		Properties props = new Properties();
		for (String[] status : statuses) {
			props.setProperty(status[0], status[1]);
		}
		return props;
	}

	private ServerInfo createServerInfo(String serverName, int serverPort, int serverThreadCount) {
		return new ServerInfo(serverName, serverPort, serverThreadCount);
	}

	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfHandleWithNullContext() throws Exception {
		handler.handle(null, createGetRequest(), createEmptyResponse());
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfHandleWithNullRequest() throws Exception {
		handler.handle(context, null, createEmptyResponse());
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfHandleWithNullResponse() throws Exception {
		handler.handle(context, createGetRequest(), null);
	}
	
	@Test
	public void setsResponseStatus400IfRequestMethodNotGet() throws Exception {
		HttpResponseStub response =  createEmptyResponse();
		
		handler.handle(context, createPutRequest(), response);
		
		assertThat(response.getStatus(), equalTo(400));
	}
	
	@Test
	public void doesNotSetResponseBodyIfRequestMethodNotGet() throws Exception {
		HttpResponseStub response =  createEmptyResponse();
		
		handler.handle(context, createPutRequest(), response);
		
		assertThat(response.getBody(), nullValue());
	}
	
	@Test
	public void setsResponseBodyIfRequestMethodIsGet() throws Exception {
		String responseBody = "body content";
		HttpResponseStub response =  createEmptyResponse();
		when(templateManager.processTemplate(Mockito.anyString(), Mockito.any())).thenReturn(responseBody);
		
		handler.handle(context, createGetRequest(), response);
		
		assertThat(response.getBody(), equalTo(responseBody));
	}
	
	@Test
	public void createsTemplateDataForResponseBody() throws Exception {
		HttpResponseStub response =  createEmptyResponse();
		when(templateManager.processTemplate(Mockito.anyString(), Mockito.any())).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				String templateName = invocation.getArgument(0);
				Map<String, Object> templateData = invocation.getArgument(1);
				
				assertThat(templateName, equalTo("server-info.html"));
				assertThat(templateData.get("SERVER-PORT"), equalTo(SERVER_PORT));
				assertThat(templateData.get("SERVER-NAME"), equalTo(SERVER_NAME));
				assertThat(templateData.get("THREAD-COUNT"), equalTo(SERVER_THREAD_COUNT));
				assertThat(templateData.get("SUPPORTED-REQUEST-METHODS"), equalTo(SUPPORTED_METHODS));
				String statuses = templateData.get("SUPPORTED-RESPONSE-STATUSES").toString();
				assertThat(statuses, containsString("200"));
				assertThat(statuses, containsString("400"));
				
				return "";
			}
		});
		
		handler.handle(context, createGetRequest(), response);
	}
	
	@Test
	public void setsTemplateArgThreadCountToUnlimitedIfZero() throws Exception {
		HttpResponseStub response =  createEmptyResponse();
		when(context.getServerInfo()).thenReturn(createServerInfo(SERVER_NAME, SERVER_PORT, 0));
		when(templateManager.processTemplate(Mockito.anyString(), Mockito.any())).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Map<String, Object> templateData = invocation.getArgument(1);
				
				assertThat(templateData.get("THREAD-COUNT"), equalTo("UNLIMITED"));
				
				return "";
			}
		});
		
		handler.handle(context, createGetRequest(), response);
	}
	
	private static HttpRequest createGetRequest() {
		return new HttpRequestStub("GET");
	}
	
	private static HttpRequest createPutRequest() {
		return new HttpRequestStub("PUT");
	}
	
	private static HttpResponseStub createEmptyResponse() {
		return new HttpResponseStub();
	}
	
	private static class HttpRequestStub implements HttpRequest {
		private String method;

		HttpRequestStub(String method) {
			this.method = method;
		}

		@Override
		public String getStartingLine() {
			return null;
		}

		@Override
		public String getMethod() {
			return method;
		}

		@Override
		public String getUri() {
			return null;
		}

		@Override
		public String getHttpVersion() {
			return null;
		}

		@Override
		public String getRemoteAddress() {
			return null;
		}

		@Override
		public Map<String, String> getHeaders() {
			return null;
		}

		@Override
		public Map<String, String> getParameters() {
			return null;
		}
	}
	
	private static class HttpResponseStub implements HttpResponse {
		private int status;
		private String body;
		
		@Override
		public void setStatus(int status) {
			this.status = status;
		}
		@Override
		public void setHeader(String name, Object value) {
		}
		@Override
		public void setBody(String content) {
			this.body = content;
		}
		@Override
		public void setBody(InputStream in) {
		}
		@Override
		public void setBody(Reader reader) {
		}
		protected int getStatus() {
			return status;
		}
		protected String getBody() {
			return body;
		}
	}

}

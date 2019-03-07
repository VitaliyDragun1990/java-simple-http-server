package com.revenat.httpserver.io.handler;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.revenat.httpserver.io.HtmlTemplateManager;
import com.revenat.httpserver.io.HttpResponse;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.handler.TestJDBCHandler.Student;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TestJDBCHandlerTest {
	private static final String STUDENTS_TABLE_TEMPLATE = "students.html";
	private static final String STUDENT_ROW_TEMPLATE = "student-row.html";
	private static final long ID = 1;
	private static final String FIRST_NAME = "Jack";
	private static final String LAST_NAME = "Smith";
	private static final int AGE = 25;
	private static final Student JACK = new Student(ID, FIRST_NAME, LAST_NAME, AGE);
	
	@Mock
	private HttpServerContext context;
	@Mock
	private HtmlTemplateManager templateManager;
	@Mock
	private EntityProvider<Student> studentProvider;
	
	private TestJDBCHandler handler;
	
	@Before
	public void setup() {
		when(context.getHtmlTemplateManager()).thenReturn(templateManager);
		when(studentProvider.getAll(context)).thenReturn(Arrays.asList(JACK));
		handler = new TestJDBCHandler(studentProvider);
	}

	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfCreatedWithNullEntityProvider() throws Exception {
		handler = new TestJDBCHandler(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfHandleNullHttpServerContext() throws Exception {
		handler.handle(null, null, createEmptyResponse());
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfHandleNullHttpResponse() throws Exception {
		handler.handle(context, null, null);
	}
	
	@Test
	public void buildsStudentRowTemplateForEachStudent() throws Exception {
		when(templateManager.processTemplate(Mockito.contains(STUDENT_ROW_TEMPLATE), Mockito.any()))
		.thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				String templateName = invocation.getArgument(0);
				Map<String, Object> templateData = invocation.getArgument(1);
				
				assertThat(templateName, equalTo(STUDENT_ROW_TEMPLATE));
				assertThat(templateData.get("ID"), equalTo(ID));
				assertThat(templateData.get("FIRST-NAME"), equalTo(FIRST_NAME));
				assertThat(templateData.get("LAST-NAME"), equalTo(LAST_NAME));
				assertThat(templateData.get("AGE"), equalTo(AGE));
				
				return "";
			}
		});
		
		handler.handle(context, null, createEmptyResponse());
		verify(templateManager, atLeast(1)).processTemplate(Mockito.contains(STUDENT_ROW_TEMPLATE), Mockito.any());
	}
	
	@Test
	public void buildsStudentsTemplateForAllStudent() throws Exception {
		when(templateManager.processTemplate(Mockito.contains(STUDENT_ROW_TEMPLATE), Mockito.any()))
		.thenReturn("Students data");
		when(templateManager.processTemplate(Mockito.contains(STUDENTS_TABLE_TEMPLATE), Mockito.any()))
		.thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				String templateName = invocation.getArgument(0);
				Map<String, Object> templateData = invocation.getArgument(1);
				
				assertThat(templateName, equalTo(STUDENTS_TABLE_TEMPLATE));
				assertThat(templateData.get("TABLE-BODY"), equalTo("Students data"));
				
				return "";
			}
		});
		
		handler.handle(context, null, createEmptyResponse());
		verify(templateManager, times(1)).processTemplate(Mockito.contains(STUDENTS_TABLE_TEMPLATE), Mockito.any());
	}
	
	@Test
	public void writesResponseBody() throws Exception {
		String responseBody = "Response body";
		when(templateManager.processTemplate(Mockito.contains(STUDENTS_TABLE_TEMPLATE), Mockito.any()))
		.thenReturn(responseBody);
		
		StubHttpResponse testResponse = createEmptyResponse();
		handler.handle(context, null, testResponse);
		assertThat(testResponse.getBody(), equalTo(responseBody));
	}
	
	private StubHttpResponse createEmptyResponse() {
		return new StubHttpResponse();
	}
	
	private static class StubHttpResponse implements HttpResponse {
		private String body = null;
		
		@Override
		public void setStatus(int status) {
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
		
		String getBody() {
			return this.body;
		}
	}

}

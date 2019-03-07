package com.revenat.httpserver.io.handler;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.io.Reader;

import org.junit.Test;

import com.revenat.httpserver.io.HttpHandler;
import com.revenat.httpserver.io.HttpResponse;

public class HelloWorldHttpHandlerTest {

	@Test
	public void setsResponseBodyContentToHelloWorldString() throws Exception {
		HttpHandler handler = new HelloWorldHttpHandler();
		StubHttpResponse response = createStubResponse();
		
		handler.handle(null, null, response);
		
		assertThat(response.getBody(), equalTo("Hello world"));
	}

	private StubHttpResponse createStubResponse() {
		return new StubHttpResponse();
	}

	private static class StubHttpResponse implements HttpResponse {
		private String body;
		
		@Override
		public void setStatus(int status) {
		}
		
		@Override
		public void setHeader(String name, Object value) {

		}
		
		@Override
		public void setBody(Reader reader) {

		}
		
		@Override
		public void setBody(InputStream in) {

		}
		
		@Override
		public void setBody(String content) {
			this.body = content;
		}
		
		String getBody() {
			return body;
		}
	}
}

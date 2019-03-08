package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.exception.HttpServerException;
import com.revenat.httpserver.io.handler.TestJDBCHandler.Student;

@RunWith(MockitoJUnitRunner.Silent.class)
public class JDBCStudentProviderTest {
	private static final BasicDataSource DATA_SOURCE = createDataSource();

	@Mock
	private HttpServerContext context;
	@Mock
	private DataSource mockDataSource;
	
	private JDBCStudentProvider provider;
	
	@Rule
	public ExpectedException expected = ExpectedException.none();
	
	@AfterClass
	public static void teardown() throws SQLException {
		if (!DATA_SOURCE.isClosed()) {
			DATA_SOURCE.close();
		}
	}
	
	@Before
	public void setup() {
		provider = new JDBCStudentProvider();
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsNullPointerExceptionIfGetStudentsWithNullContext() throws Exception {
		provider.getAll(null);
	}

	@Test
	public void returnsAllStudentsFromTheDatabase() throws Exception {
		when(context.getDataSource()).thenReturn(DATA_SOURCE);
		List<Student> students = provider.getAll(context);
		
		assertThat(students, hasSize(2));
		assertThat(students, hasItems(hasProperty("id", is(1L)),
									  hasProperty("firstName", is("Jack")),
									  hasProperty("lastName", is("Smith")),
									  hasProperty("age", is(25))));
		assertThat(students, hasItems(hasProperty("id", is(2L)),
				  					  hasProperty("firstName", is("Anna")),
				  					  hasProperty("lastName", is("Smith")),
				  					  hasProperty("age", is(20))));
	}
	
	@Test
	public void throwsHttpServerExceptionIfErrorOccurredDuringWorkWithDatabase() throws Exception {
		when(context.getDataSource()).thenReturn(mockDataSource);
		when(mockDataSource.getConnection()).thenThrow(new SQLException("Some error occurred"));
		expected.expect(HttpServerException.class);
		expected.expectMessage(containsString("Error with database"));
		
		provider.getAll(context);
	}
	
	private static BasicDataSource createDataSource() {
		BasicDataSource ds = new BasicDataSource();
		
		ds.setDefaultAutoCommit(false);
		ds.setRollbackOnReturn(true);
		ds.setDriverClassName("org.h2.Driver");
		ds.setUrl("jdbc:h2:mem:test;INIT=runscript from 'src/test/resources/create.sql'\\;"
				+ "runscript from 'src/test/resources/init.sql'");
		ds.setInitialSize(1);
		ds.setMaxTotal(2);
	
		return ds;
	}
	
}

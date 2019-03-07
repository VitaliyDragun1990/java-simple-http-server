package com.revenat.httpserver.io.utils;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JDBCUtilsTest {
	private static final int STUDENT_AGE = 25;
	private static final String STUDENT_NAME = "Jack";
	private static final long STUDENT_ID = 10L;
	private static final String SELECT_QUERY_WITHOUT_PARAMS = "select from student";
	private static final String SELECT_QUERY_WITH_PARAMS = "select from student where id=?, name=?";
	private static final String UPDATE_QUERY_WITHOUT_PARAMS = "delete from student";
	private static final String UPDATE_QUERY_WITH_PARAMS = "update student set name=? where id=?";
	private static final String INSERT_QUERY_WITH_PARAMS = "insert into student values(?,?,?)";
	private static final String INSERT_QUERY_WITHOUT_PARAMS = "insert into student values(10,'Jack',25)";
	
	@Mock
	private Connection connection;
	@Mock
	private PreparedStatement statement;
	@Mock
	private ResultSet resultSet;
	@Mock
	private ResultSetHandler<Object> handler;
	
	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfSelectWihtNullConnection() throws Exception {
		JDBCUtils.select(null, SELECT_QUERY_WITHOUT_PARAMS, handler);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfSelectWihtNullQuery() throws Exception {
		JDBCUtils.select(connection, null, handler);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfSelectWihtNullHandler() throws Exception {
		JDBCUtils.select(connection, SELECT_QUERY_WITHOUT_PARAMS, null);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfInsertWihtNullConnection() throws Exception {
		JDBCUtils.insert(null, INSERT_QUERY_WITHOUT_PARAMS, handler);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfInsertWihtNullQuery() throws Exception {
		JDBCUtils.insert(connection, null, handler);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfInsertWihtNullHandler() throws Exception {
		JDBCUtils.insert(connection, INSERT_QUERY_WITHOUT_PARAMS, null);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfUpdateWihtNullConnection() throws Exception {
		JDBCUtils.executeUpdate(null, UPDATE_QUERY_WITHOUT_PARAMS);
	}
	
	@Test(expected = NullPointerException.class)
	public void throwsExceptionIfUpdateWihtNullQuery() throws Exception {
		JDBCUtils.executeUpdate(connection, null);
	}
	
	@Test
	public void returnsResultWhenSelectWithoutParams() throws Exception {
		Object expectedResult = new Object();
		when(connection.prepareStatement(SELECT_QUERY_WITHOUT_PARAMS)).thenReturn(statement);
		when(statement.executeQuery()).thenReturn(resultSet);
		when(handler.handle(resultSet)).thenReturn(expectedResult);
		
		Object actualResult = JDBCUtils.select(connection, SELECT_QUERY_WITHOUT_PARAMS, handler);
		
		verify(connection, times(1)).prepareStatement(SELECT_QUERY_WITHOUT_PARAMS);
		verify(statement).executeQuery();
		verify(handler).handle(resultSet);
		verify(statement, never()).setObject(anyInt(), any());
		verify(resultSet).close();
		verify(statement).close();
		assertSame(expectedResult, actualResult);
	}
	
	@Test
	public void returnsResultWhenSelectWithParams() throws Exception {
		Object expectedResult = new Object();
		when(connection.prepareStatement(SELECT_QUERY_WITH_PARAMS)).thenReturn(statement);
		when(statement.executeQuery()).thenReturn(resultSet);
		when(handler.handle(resultSet)).thenReturn(expectedResult);
		
		Object actualResult = JDBCUtils.select(connection, SELECT_QUERY_WITH_PARAMS, handler, STUDENT_ID, STUDENT_NAME);
		
		verify(connection, times(1)).prepareStatement(SELECT_QUERY_WITH_PARAMS);
		verify(statement).executeQuery();
		verify(handler).handle(resultSet);
		verify(statement, times(1)).setObject(1, STUDENT_ID);
		verify(statement, times(1)).setObject(2, STUDENT_NAME);
		verify(resultSet).close();
		verify(statement).close();
		assertSame(expectedResult, actualResult);
	}
	
	@Test(expected = SQLException.class)
	public void throwsSQLExceptionIfErrorOccursDuringSelect() throws Exception {
		when(connection.prepareStatement(SELECT_QUERY_WITHOUT_PARAMS)).thenThrow(new SQLException("Something went wrong."));
		
		JDBCUtils.select(connection, SELECT_QUERY_WITHOUT_PARAMS, handler);
	}

	@Test
	public void returnsResultWhenInsertWithoutParams() throws Exception {
		Object expectedResult = new Object();
		when(connection.prepareStatement(INSERT_QUERY_WITHOUT_PARAMS, 1)).thenReturn(statement);
		when(statement.executeUpdate()).thenReturn(1);
		when(statement.getGeneratedKeys()).thenReturn(resultSet);
		when(handler.handle(resultSet)).thenReturn(expectedResult);
		
		Object actualResult = JDBCUtils.insert(connection, INSERT_QUERY_WITHOUT_PARAMS, handler);
		
		verify(connection, times(1)).prepareStatement(INSERT_QUERY_WITHOUT_PARAMS, 1);
		verify(statement).executeUpdate();
		verify(statement).getGeneratedKeys();
		verify(handler).handle(resultSet);
		verify(statement, never()).setObject(anyInt(), any());
		verify(resultSet).close();
		verify(statement).close();
		assertSame(expectedResult, actualResult);
	}
	
	@Test
	public void returnsResultWhenInsertWithParams() throws Exception {
		Object expectedResult = new Object();
		when(connection.prepareStatement(INSERT_QUERY_WITH_PARAMS, 1)).thenReturn(statement);
		when(statement.executeUpdate()).thenReturn(1);
		when(statement.getGeneratedKeys()).thenReturn(resultSet);
		when(handler.handle(resultSet)).thenReturn(expectedResult);
		
		Object actualResult = JDBCUtils.insert(connection, INSERT_QUERY_WITH_PARAMS, handler, STUDENT_ID, STUDENT_NAME, STUDENT_AGE);
		
		verify(connection, times(1)).prepareStatement(INSERT_QUERY_WITH_PARAMS, 1);
		verify(statement).executeUpdate();
		verify(statement).getGeneratedKeys();
		verify(handler).handle(resultSet);
		verify(statement, times(1)).setObject(1, STUDENT_ID);
		verify(statement, times(1)).setObject(2, STUDENT_NAME);
		verify(statement, times(1)).setObject(3, STUDENT_AGE);
		verify(resultSet).close();
		verify(statement).close();
		assertSame(expectedResult, actualResult);
	}
	
	@Test
	public void throwsSQLExceptionIfCanNotInsertNewRow() throws Exception {
		when(connection.prepareStatement(INSERT_QUERY_WITHOUT_PARAMS, 1)).thenReturn(statement);
		when(statement.executeUpdate()).thenReturn(0);
		expected.expect(SQLException.class);
		expected.expectMessage(containsString("Can not insert new row"));
		
		JDBCUtils.insert(connection, INSERT_QUERY_WITHOUT_PARAMS, handler);
	}
	
	@Test
	public void throwsSQLExceptionIfErrorOccursDuringInsert() throws Exception {
		when(connection.prepareStatement(INSERT_QUERY_WITHOUT_PARAMS, 1)).thenThrow(new SQLException("Something went wrong"));
		expected.expect(SQLException.class);
		expected.expectMessage(containsString("Something went wrong"));
		
		JDBCUtils.insert(connection, INSERT_QUERY_WITHOUT_PARAMS, handler);
	}
	
	@Test
	public void executesUpdateWithoutParams() throws Exception {
		int expectedUpdateCound = 1;
		when(connection.prepareStatement(UPDATE_QUERY_WITHOUT_PARAMS)).thenReturn(statement);
		when(statement.executeUpdate()).thenReturn(1);
		
		int actualUpdateCount = JDBCUtils.executeUpdate(connection, UPDATE_QUERY_WITHOUT_PARAMS);
		
		verify(connection, times(1)).prepareStatement(UPDATE_QUERY_WITHOUT_PARAMS);
		verify(statement).executeUpdate();
		verify(statement, never()).setObject(anyInt(), any());
		verify(statement).close();
		assertSame(expectedUpdateCound, actualUpdateCount);
	}
	
	@Test
	public void executesUpdateWithParams() throws Exception {
		int expectedUpdateCound = 1;
		when(connection.prepareStatement(UPDATE_QUERY_WITH_PARAMS)).thenReturn(statement);
		when(statement.executeUpdate()).thenReturn(1);
		
		int actualUpdateCount = JDBCUtils.executeUpdate(connection, UPDATE_QUERY_WITH_PARAMS, STUDENT_NAME, STUDENT_ID);
		
		verify(connection, times(1)).prepareStatement(UPDATE_QUERY_WITH_PARAMS);
		verify(statement).executeUpdate();
		verify(statement, times(1)).setObject(1, STUDENT_NAME);
		verify(statement, times(1)).setObject(2, STUDENT_ID);
		verify(statement).close();
		assertSame(expectedUpdateCound, actualUpdateCount);
	}
	
	@Test
	public void throwsSQLExceptionIfErrorOccursDuringUpdate() throws Exception {
		when(connection.prepareStatement(UPDATE_QUERY_WITHOUT_PARAMS)).thenThrow(new SQLException("Something went wrong"));
		expected.expect(SQLException.class);
		expected.expectMessage(containsString("Something went wrong"));
		
		JDBCUtils.executeUpdate(connection, UPDATE_QUERY_WITHOUT_PARAMS);
	}
}
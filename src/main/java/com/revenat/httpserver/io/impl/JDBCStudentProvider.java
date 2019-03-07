package com.revenat.httpserver.io.impl;

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.exception.HttpServerException;
import com.revenat.httpserver.io.handler.EntityProvider;
import com.revenat.httpserver.io.handler.TestJDBCHandler.Student;
import com.revenat.httpserver.io.utils.JDBCUtils;
import com.revenat.httpserver.io.utils.ResultSetHandler;

/**
 * Implementation of the {@link EntityProvider} responsible for
 * retrieving {@link Student} objects from the relational database.
 * 
 * @author Vitaly Dragun
 *
 */
public class JDBCStudentProvider implements EntityProvider<Student> {
	private static final String SELECT_STUDENTS = "select * from student";
	private static final ResultSetHandler<List<Student>> STUDENTS_HANDLER = new StudentResultSetHandler();

	@Override
	public List<Student> getAll(HttpServerContext context) {
		requireNonNull(context, "context can not be null");
		
		try (Connection conn = context.getDataSource().getConnection()) {
			return Collections.unmodifiableList(JDBCUtils.select(conn, SELECT_STUDENTS, STUDENTS_HANDLER));

		} catch (SQLException e) {
			throw new HttpServerException("Error with database: " + e.getMessage(), e);
		}
	}
	
	private static class StudentResultSetHandler implements ResultSetHandler<List<Student>> {

		@Override
		public List<Student> handle(ResultSet rs) throws SQLException {
			List<Student> students = new ArrayList<>();
			while (rs.next()) {
				int id = rs.getInt("id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				int age = rs.getInt("age");
				students.add(new Student(id, firstName, lastName, age));
			}
			return students;
		}
		
	}

}

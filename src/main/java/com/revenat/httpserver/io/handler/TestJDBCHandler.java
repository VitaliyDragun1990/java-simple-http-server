package com.revenat.httpserver.io.handler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.revenat.httpserver.io.HttpHandler;
import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.HttpResponse;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.exception.HttpServerException;
import com.revenat.httpserver.io.utils.DataUtils;
import com.revenat.httpserver.io.utils.JDBCUtils;
import com.revenat.httpserver.io.utils.ResultSetHandler;


/**
 * Simple implementation of the {@link HttpHandler} that demonstrates how it can
 * be possible to work with database from the handler.
 * 
 * @author Vitaly Dragun
 *
 */
public class TestJDBCHandler implements HttpHandler {
	private static final String STUDENTS_TEMPLATE = "students.html";
	private static final String SELECT_STUDENTS = "select * from student";
	private static final ResultSetHandler<List<Student>> STUDENTS_HANDLER = new StudentResultSetHandler();

	@Override
	public void handle(HttpServerContext context, HttpRequest request, HttpResponse response) throws IOException {
		try (Connection conn = context.getDataSource().getConnection()) {
			List<Student> students = JDBCUtils.select(conn, SELECT_STUDENTS, STUDENTS_HANDLER);
			Map<String, Object> tabelBody = getTableBody(students, context);
			response.setBody(context.getHtmlTemplateManager().pocessTemplate(STUDENTS_TEMPLATE, tabelBody));

		} catch (SQLException e) {
			throw new HttpServerException("Error with database: " + e.getMessage(), e);
		}

	}
	
	private Map<String, Object> getTableBody(List<Student> students, HttpServerContext context) {
		StringBuilder tableBody = new StringBuilder("");
		for (Student student : students) {
			tableBody.append(getRowBody(context, student));
		}
		
		return DataUtils.buildMap(new Object[][]{
			{ "TABLE-BODY", tableBody }
		});
	}
	

	private String getRowBody(HttpServerContext context, Student student) {
		Map<String, Object> studentRowArgs = DataUtils.buildMap(new Object[][] {
			{ "ID" , student.getId() },
			{ "FIRST-NAME" , student.getFirstName() },
			{ "LAST-NAME" , student.getLastName() },
			{ "AGE" , student.getAge() },
		});
		return context.getHtmlTemplateManager().pocessTemplate("student-row.html", studentRowArgs);
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
	
	@SuppressWarnings("unused")
	private static class Student {
		private long id;
		private String firstName;
		private String lastName;
		private int age;

		public Student(long id, String firstName, String lastName, int age) {
			setId(id);
			setFirstName(firstName);
			setLastName(lastName);
			setAge(age);
		}
		
		public void setId(long id) {
			this.id = id;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public void setLastName(String lastName) {
			if (lastName.length() > 1) {
				this.lastName = Character.toUpperCase(lastName.charAt(0)) + lastName.substring(1).toLowerCase();
			} else {
				this.lastName = lastName.toUpperCase();
			}
		}

		public void setFirstName(String firstName) {
			if (firstName.length() > 1) {
				this.firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
			} else {
				this.firstName = firstName.toUpperCase();
			}
		}
		
		public long getId() {
			return this.id;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public int getAge() {
			return age;
		}

		@Override
		public String toString() {
			return "Student [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", age=" + age + "]";
		}

		@Override
		public int hashCode() {
			return Objects.hash(age, firstName, id, lastName);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Student other = (Student) obj;
			return age == other.age && Objects.equals(firstName, other.firstName) && id == other.id
					&& Objects.equals(lastName, other.lastName);
		}
	}

}

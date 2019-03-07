package com.revenat.httpserver.io.handler;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.revenat.httpserver.io.HttpHandler;
import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.HttpResponse;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.utils.DataUtils;

/**
 * Simple implementation of the {@link HttpHandler} that demonstrates how it can
 * be possible to work with database from the handler.
 * 
 * @author Vitaly Dragun
 *
 */
public class TestJDBCHandler implements HttpHandler {
	private static final String STUDENT_ROW_TEMPLATE = "student-row.html";
	private static final String STUDENTS_TEMPLATE = "students.html";

	private final EntityProvider<Student> studentProvider;

	public TestJDBCHandler(EntityProvider<Student> studentProvider) {
		requireNonNull(studentProvider, "studentProvider ca not be null");
		this.studentProvider = studentProvider;
	}

	@Override
	public void handle(HttpServerContext context, HttpRequest request, HttpResponse response) throws IOException {
		requireNonNull(context, "context can not be null");
		requireNonNull(response, "response can not be null");
		
		List<Student> students = studentProvider.getAll(context);
		Map<String, Object> tabelBody = getTableBody(students, context);
		response.setBody(context.getHtmlTemplateManager().processTemplate(STUDENTS_TEMPLATE, tabelBody));

	}

	private Map<String, Object> getTableBody(List<Student> students, HttpServerContext context) {
		StringBuilder tableBody = new StringBuilder("");
		for (Student student : students) {
			tableBody.append(getRowBody(context, student));
		}

		return DataUtils.buildMap(new Object[][] { 
			{ "TABLE-BODY", tableBody.toString() } 
			});
	}

	private String getRowBody(HttpServerContext context, Student student) {
		Map<String, Object> studentRowArgs = DataUtils
				.buildMap(new Object[][] {
					{ "ID", student.getId() },
					{ "FIRST-NAME", student.getFirstName() },
					{ "LAST-NAME", student.getLastName() },
					{ "AGE", student.getAge() }, 
					});
		return context.getHtmlTemplateManager().processTemplate(STUDENT_ROW_TEMPLATE, studentRowArgs);
	}

	public static class Student {
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
	}

}

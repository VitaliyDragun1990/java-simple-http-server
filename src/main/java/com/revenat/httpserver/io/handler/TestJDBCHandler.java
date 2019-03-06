package com.revenat.httpserver.io.handler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.revenat.httpserver.io.HttpHandler;
import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.HttpResponse;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.exception.HttpServerException;

/**
 * Simple implementation of the {@link HttpHandler} that demonstrates how it can
 * be possible to work with database from the handler.
 * 
 * @author Vitaly Dragun
 *
 */
public class TestJDBCHandler implements HttpHandler {

	@Override
	public void handle(HttpServerContext context, HttpRequest request, HttpResponse response) throws IOException {
		try (Connection conn = context.getDataSource().getConnection();
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery("select count(*) from student")) {
			if (rs.next()) {
				response.setBody(rs.getString(1));
			} else {
				response.setBody("null");
			}

		} catch (SQLException e) {
			throw new HttpServerException("Error with database: " + e.getMessage(), e);
		}

	}

}

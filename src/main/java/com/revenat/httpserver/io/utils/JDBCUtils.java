package com.revenat.httpserver.io.utils;

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Contains helper methods to work with JDBC.
 * 
 * @author Vitaly Dragun
 *
 */
public final class JDBCUtils {

	private JDBCUtils() {
	}

	/**
	 * Creates and executes SELECT SQL statement, using specified parameters.
	 * 
	 * @param conn             active connection to the database
	 * @param sql              SQL query to execute against the database
	 * @param resultSetHandler implementation of the {@link ResultSetHandler} with
	 *                         custom logic to handle returned result set
	 * @param params           optional parameters to be passed into generated SQL
	 *                         query
	 * @return custom type that represents result of handling result set returned
	 *         from executed SELECT statement
	 * @throws NullPointerException if any of the arguments except {@code params} is null.
	 * @throws SQLException if some sort of error occurs during work with the database.
	 */
	public static <T> T select(Connection conn, String sql, ResultSetHandler<T> resultSetHandler, Object... params)
			throws SQLException {
		retuireNotNull(conn, sql, resultSetHandler);
		
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			populatePreparedStatement(ps, params);
			try (ResultSet rs = ps.executeQuery()) {
				return resultSetHandler.handle(rs);
			}
		}
	}

	/**
	 * Creates and executes INSERT SQL statement, using specified parameters.
	 * 
	 * @param conn             active connection to the database
	 * @param sql              SQL query to execute against the database
	 * @param resultSetHandler implementation of the {@link ResultSetHandler} with
	 *                         custom logic to handle returned result set with
	 *                         auto-generated keys after success insert
	 * @param params           optional parameters to be passed into generated SQL
	 *                         query
	 * @return custom type that represents result of handling result set returned
	 *         from executed INSERT statement (auto-generated keys)
	 * @throws NullPointerException if any of the arguments except {@code params} is null.
	 * @throws SQLException if some sort of error occurs during work with the database.
	 */
	public static <T> T insert(Connection conn, String sql, ResultSetHandler<T> resultSetHandler, Object... params)
			throws SQLException {
		retuireNotNull(conn, sql, resultSetHandler);
		
		try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
			populatePreparedStatement(ps, params);
			int result = ps.executeUpdate();
			if (result != 1) {
				throw new SQLException("Can not insert new row to the database. Result=" + result);
			}
			try (ResultSet rs = ps.getGeneratedKeys()) {
				return resultSetHandler.handle(rs);
			}
		}
	}

	/**
	 * Creates and executes UPDATE/DELETE SQL statement, using specified parameters.
	 * 
	 * @param conn   active connection to the database
	 * @param sql    SQL query to execute against the database
	 * @param params optional parameters to be inserted into generated SQL query
	 * @return number of updated rows
	 * @throws NullPointerException if any of the arguments except {@code params} is null.
	 * @throws SQLException if some sort of error occurs during work with the database.
	 */
	public static int executeUpdate(Connection conn, String sql, Object... params) throws SQLException {
		retuireNotNull(conn, sql);
		
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			populatePreparedStatement(ps, params);
			return ps.executeUpdate();
		}
	}

	private static void populatePreparedStatement(PreparedStatement ps, Object... params) throws SQLException {
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
		}
	}
	
	private static void retuireNotNull(Connection conn, String sql, ResultSetHandler<?>... handlers) {
		requireNonNull(conn, "Connection can not be null");
		requireNonNull(sql, "Sql string can not be null");
		if (handlers.length > 0) {
			requireNonNull(handlers[0], "Result set handler can not be null");
		}
		
	}
}

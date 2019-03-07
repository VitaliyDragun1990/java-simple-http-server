package com.revenat.httpserver.io.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents handler with method {@code #handle(ResultSet)} whose role
 * is to map given {@link ResultSet} instance to some generic type <T>
 * @author Vitaly Dragun
 *
 * @param <T> generic type
 */
@FunctionalInterface
public interface ResultSetHandler<T> {
	T handle(ResultSet rs) throws SQLException;
}

package com.revenat.httpserver.io.handler;

import java.util.List;

import com.revenat.httpserver.io.HttpServerContext;

/**
 * Component responsible for retrieving abstract kind of entities from the
 * abstract data store.
 * 
 * @author Vitaly Dragun
 *
 * @param <T> represent abstract kind of entity
 */
public interface EntityProvider<T> {

	List<T> getAll(HttpServerContext context);
}

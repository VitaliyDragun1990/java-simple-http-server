package com.revenat.httpserver.io.config;

import com.revenat.httpserver.io.HttpHandler;

/**
 * Component that represents dispatcher for {@link HttpHandler} components.
 * It's responsible for coordinating different {@link HttpHandler} implementations.
 * @author Vitaly Dragun
 *
 */
public interface HttpRequestDispatcher extends HttpHandler {

}

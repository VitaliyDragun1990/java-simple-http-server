package com.revenat.httpserver.io.config;

import com.revenat.httpserver.io.HttpHandler;

/**
 * Component that represents dispatcher for {@link HttpHandler} components. It's
 * responsible for choosing the most appropriate {@link HttpHandler} to handle
 * given {@link HttpRequest}
 * 
 * @author Vitaly Dragun
 *
 */
public interface HttpRequestDispatcher extends HttpHandler {

}

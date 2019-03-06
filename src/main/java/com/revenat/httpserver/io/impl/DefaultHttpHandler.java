package com.revenat.httpserver.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;

import com.revenat.httpserver.io.HttpHandler;
import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.HttpResponse;
import com.revenat.httpserver.io.HttpServerContext;
import com.revenat.httpserver.io.utils.DataUtils;

/**
 * Default {@link HttpHandler} implementation, responsible for serving
 * requested resources from server's root directory.
 * 
 * @author Vitaly Dragun
 *
 */
class DefaultHttpHandler implements HttpHandler {
	private static final String BODY = "BODY";
	private static final String HEADER = "HEADER";
	private static final String TITLE = "TITLE";
	private static final String SIMPLE_TEMPLATE = "simple.html";
	private static final String DIR_ENTRY = "<a href='/%s'>%s</a><br />%n";

	@Override
	public void handle(HttpServerContext context, HttpRequest request, HttpResponse response) throws IOException {
		Path resourcePath = Paths.get(context.getRootPath().toString(), request.getUri());

		if (!resourcePath.toFile().exists()) {
			handleResourceNotFound(response);
		} else if (resourcePath.toFile().isFile()) {
			handleFileRequest(context, response, resourcePath);
		} else if (resourcePath.toFile().isDirectory()) {
			hadleDirectoryRequest(response, resourcePath, context);
		}

	}

	private void handleResourceNotFound(HttpResponse response) {
		response.setStatus(404);
	}

	private void handleFileRequest(HttpServerContext context, HttpResponse response, Path resourcePath) throws IOException {
		setResponseHeaders(context, response, resourcePath);

		writeFileContentToResponseBody(response, resourcePath);
	}

	private void setResponseHeaders(HttpServerContext context, HttpResponse response, Path resourcePath)
			throws IOException {
		String fileExtension = FilenameUtils.getExtension(resourcePath.toString());
		String contentType = context.getContentType(fileExtension);
		response.setHeader("Content-Type", contentType);

		response.setHeader("Last-Modified", Files.getLastModifiedTime(resourcePath, LinkOption.NOFOLLOW_LINKS));

		Integer expireDays = context.getExpiresDaysForResource(fileExtension);
		if (expireDays != null) {
			response.setHeader("Expires", ZonedDateTime.now().plus(expireDays, ChronoUnit.DAYS));
		}
	}

	private void hadleDirectoryRequest(HttpResponse response, Path dir, HttpServerContext context)
			throws IOException {
		String content = getDirectoryContent(dir, context);
		response.setBody(content);
	}

	private String getDirectoryContent(Path dir, HttpServerContext context) throws IOException {
		StringBuilder htmlBody = new StringBuilder();
		try (Stream<Path> directoryContent = Files.list(dir)) {
			directoryContent.forEachOrdered(path -> {
				String entryName = path.getFileName().toString();
				String relativePath = getRelativePath(context.getRootPath(), path);
				if (path.toFile().isDirectory()) {
					htmlBody.append(String.format(DIR_ENTRY, relativePath, entryName + "/"));
				} else {
					htmlBody.append(String.format(DIR_ENTRY, relativePath, entryName));
				}
			});
		}
		
		Map<String, Object> templateArgs = DataUtils.buildMap(new Object[][] {
			{TITLE, "File list for " + dir.getFileName()},
			{HEADER, "File list for " + dir.getFileName()},
			{BODY, htmlBody}
		});
		
		return context.getHtmlTemplateManager().pocessTemplate(SIMPLE_TEMPLATE, templateArgs);
	}

	private String getRelativePath(Path root, Path resource) {
		if (root.equals(resource)) {
			return "";
		}
		String rootPath = root.toString();
		String resourcePath = resource.toString();
		return resourcePath.substring(rootPath.length() + 1);
	}


	private void writeFileContentToResponseBody(HttpResponse response, Path resourcePath) throws IOException {
		try (InputStream in = Files.newInputStream(resourcePath, StandardOpenOption.READ)) {
			response.setBody(in);
		}
	}
}

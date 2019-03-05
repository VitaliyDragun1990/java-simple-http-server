package com.revenat.httpserver.io.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.revenat.httpserver.io.HttpHandler;
import com.revenat.httpserver.io.HttpRequest;
import com.revenat.httpserver.io.HttpResponse;
import com.revenat.httpserver.io.HttpServerContext;

/**
 * Component responsible for handling GET request for some static content that
 * can be found in the server's root directory
 * 
 * @author Vitaly Dragun
 *
 */
class StaticResourcesGetHttpHandler implements HttpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(StaticResourcesGetHttpHandler.class);
	private static final String DIR_ENTRY = "<li><a href='/%s'>%s</a></li>";

	@Override
	public void handle(HttpServerContext context, HttpRequest request, HttpResponse response) throws IOException {
		Path resourcePath = Paths.get(context.getRootPath().toString(), request.getUri());

		if (!resourcePath.toFile().exists()) {
			handleResourceNotFound(request, response);
		} else if (resourcePath.toFile().isFile()) {
			handleResourceIsFile(context, request, response, resourcePath);
		} else if (resourcePath.toFile().isDirectory()) {
			hadleResourceIsDirectory(request, response, resourcePath, context.getRootPath());
		}

	}

	private void handleResourceNotFound(HttpRequest request, HttpResponse response) {
		response.setStatus(404);
		LOGGER.debug("Requeired static resource '{}' is not found on the server", request.getUri());
	}

	private void handleResourceIsFile(HttpServerContext context, HttpRequest request, HttpResponse response,
			Path resourcePath) throws IOException {
		String fileExtension = getFileExtension(resourcePath);
		String contentType = context.getContentType(getFileExtension(resourcePath));
		response.setHeader("Content-Type", contentType);

		response.setHeader("Last-Modified", Files.getLastModifiedTime(resourcePath));

		Integer expireDays = context.getExpiresDaysForResource(fileExtension);
		if (expireDays != null) {
			response.setHeader("Expires", expireDays);
		}

		writeFileContentToResponseBody(response, resourcePath);
		LOGGER.debug("Required static resource '{}' is found on the server", request.getUri());
	}

	private void hadleResourceIsDirectory(HttpRequest request, HttpResponse response, Path resourcePath, Path rootPath)
			throws IOException {
		List<String> directoryEntries = getDirectoryEntries(resourcePath, rootPath);

		if (!directoryEntries.isEmpty()) {
			writeDirectoryContentToRespponseBody(response, directoryEntries);
			LOGGER.debug("Required static resource '{}' is a directory with content", request.getUri());
		} else {
			LOGGER.debug("Required static resource '{}' is en empty directory", request.getUri());
		}
	}

	private static void writeDirectoryContentToRespponseBody(HttpResponse response, List<String> directoryEntries) {
		StringBuilder content = new StringBuilder("<ul>");
		for (String entry : directoryEntries) {
			content.append(entry);
		}
		content.append("</ul>");
		response.setBody(content.toString());
	}

	private static List<String> getDirectoryEntries(Path resourcePath, Path rootPath) throws IOException {
		List<String> directoryEntries = new ArrayList<>();
		try (Stream<Path> directoryContent = Files.list(resourcePath)) {
			directoryContent.forEachOrdered(path -> {
				String entryName = path.getFileName().toString();
				String relPath = getRelativePath(rootPath, path);
				if (path.toFile().isDirectory()) {
					directoryEntries.add(String.format(DIR_ENTRY, relPath, entryName + "/"));
				} else {
					directoryEntries.add(String.format(DIR_ENTRY, relPath, entryName));
				}
			});
		}
		return directoryEntries;
	}

	private static String getRelativePath(Path rootPath, Path resourcePath) {
		if (rootPath.equals(resourcePath)) {
			return "";
		}
		String rootP = rootPath.toString();
		String resPath = resourcePath.toString();
		return resPath.substring(rootP.length() + 1);
	}


	private static void writeFileContentToResponseBody(HttpResponse response, Path resourcePath) throws IOException {
		try (InputStream in = Files.newInputStream(resourcePath)) {
			response.setBody(Files.newInputStream(resourcePath));
		}
	}

	private static String getFileExtension(Path resourcePath) {
		String fileName = resourcePath.toFile().getName();
		int delimiterIndex = fileName.lastIndexOf('.');
		return fileName.substring(delimiterIndex + 1).trim();
	}

}

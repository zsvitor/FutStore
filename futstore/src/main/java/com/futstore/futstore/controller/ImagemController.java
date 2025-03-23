package com.futstore.futstore.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/uploads")
public class ImagemController {

	@Value("${app.upload.dir:${user.home}/uploads}")
	private String uploadDir;

	@GetMapping("/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		try {
			Path filePath = Paths.get(uploadDir).resolve(filename);
			File file = filePath.toFile();

			if (!file.exists()) {
				return ResponseEntity.notFound().build();
			}

			Resource resource = new FileSystemResource(file);
			String contentType = determineContentType(filename);

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	private String determineContentType(String filename) {
		if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
			return "image/jpeg";
		} else if (filename.toLowerCase().endsWith(".png")) {
			return "image/png";
		} else if (filename.toLowerCase().endsWith(".gif")) {
			return "image/gif";
		} else {
			return "application/octet-stream";
		}
	}

}
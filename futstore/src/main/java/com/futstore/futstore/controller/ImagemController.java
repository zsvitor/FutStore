package com.futstore.futstore.controller;

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

@Controller
@RequestMapping("/uploads")
public class ImagemController {

	private static final String IMAGE_DIRECTORY = "C:\\Users\\Vitor\\Desktop\\Projeto FutStore\\PI_Desenvolvimento_de_Sistemas_Orientados_a_Web\\futstore\\src\\main\\resources\\static\\uploads";

	@GetMapping("/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		File file = new File(IMAGE_DIRECTORY + File.separator + filename);

		if (!file.exists()) {
			return ResponseEntity.notFound().build();
		}
		Resource resource = new FileSystemResource(file);
		String contentType = determineContentType(filename);
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
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
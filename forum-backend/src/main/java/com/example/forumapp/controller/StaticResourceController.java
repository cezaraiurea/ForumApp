
package com.example.forumapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/uploads")
public class StaticResourceController {

    private final Path uploadPath = Paths.get("uploads");

    @GetMapping("/**")
    public ResponseEntity<Resource> serveFile(HttpServletRequest request) throws IOException {
        String uri = request.getRequestURI();
        String relativePath = uri.replaceFirst("/uploads/?", "");
        Path file = uploadPath.resolve(relativePath);

        if (!Files.exists(file)) {
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).build();
        }

        String contentType = Files.probeContentType(file);
        MediaType mediaType;

        if (contentType != null) {
            mediaType = MediaType.parseMediaType(contentType);
        } else {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        Resource resource = new UrlResource(file.toUri());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }


}

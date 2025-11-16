package com.safetransfer.files;

import java.io.IOException;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/download")
public class FileDownloadController {

  private static final Map<String, DownloadableFile> FILES = Map.of(
      "index.html", new DownloadableFile("static/index.html", MediaType.TEXT_HTML),
      "cadastro.html", new DownloadableFile("static/cadastro.html", MediaType.TEXT_HTML),
      "dashboard.html", new DownloadableFile("static/dashboard.html", MediaType.TEXT_HTML),
      "historico.html", new DownloadableFile("static/historico.html", MediaType.TEXT_HTML),
      "conta.html", new DownloadableFile("static/conta.html", MediaType.TEXT_HTML),
      "style.css", new DownloadableFile("static/style.css", MediaType.valueOf("text/css")),
      "script.js", new DownloadableFile("static/script.js", MediaType.valueOf("application/javascript"))
  );

  @GetMapping("/{fileName}")
  public ResponseEntity<Resource> download(@PathVariable String fileName) throws IOException {
    DownloadableFile file = FILES.get(fileName);
    if (file == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    ClassPathResource resource = new ClassPathResource(file.classpathLocation());
    if (!resource.exists()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    return ResponseEntity.ok()
        .contentType(file.mediaType())
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
        .body(resource);
  }

  private record DownloadableFile(String classpathLocation, MediaType mediaType) {}
}

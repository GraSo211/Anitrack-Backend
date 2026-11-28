package com.graso.anitrack.importer.controller;

import com.graso.anitrack.importer.controller.dto.ImportJobResponse;
import com.graso.anitrack.importer.controller.dto.ImportSearchResponse;
import com.graso.anitrack.importer.controller.dto.ResolveImportRequest;
import com.graso.anitrack.importer.controller.dto.StartImportRequest;
import com.graso.anitrack.importer.service.ImportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/import")
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping("")
    public ResponseEntity<?> create(
            @RequestParam("file") MultipartFile file,
            @CookieValue(value = "access_token", required = false) String token) throws IOException {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado"));
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El archivo está vacío"));
        }
        String jobId = importService.createJob(file.getInputStream(), token);
        return ResponseEntity.accepted().body(ImportJobResponse.from(importService.get(jobId)));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<ImportJobResponse> get(@PathVariable String jobId) {
        return ResponseEntity.ok(ImportJobResponse.from(importService.get(jobId)));
    }

    @PostMapping("/{jobId}/start")
    public ResponseEntity<ImportJobResponse> start(
            @PathVariable String jobId,
            @CookieValue(value = "access_token", required = false) String token,
            @RequestBody(required = false) StartImportRequest body) {
        importService.start(
                jobId,
                token,
                body == null || body.overwriteMalIds() == null ? List.of() : body.overwriteMalIds(),
                body == null || body.excludedMalIds() == null ? List.of() : body.excludedMalIds());
        return ResponseEntity.accepted().body(ImportJobResponse.from(importService.get(jobId)));
    }

    @PostMapping("/{jobId}/resolve")
    public ResponseEntity<ImportJobResponse> resolve(
            @PathVariable String jobId,
            @CookieValue(value = "access_token", required = false) String token,
            @RequestBody ResolveImportRequest body) {
        importService.resolve(jobId, token, body.decisions() == null ? List.of() : body.decisions());
        return ResponseEntity.accepted().body(ImportJobResponse.from(importService.get(jobId)));
    }

    @PostMapping("/{jobId}/retry")
    public ResponseEntity<ImportJobResponse> retry(
            @PathVariable String jobId,
            @CookieValue(value = "access_token", required = false) String token) {
        importService.retry(jobId, token);
        return ResponseEntity.accepted().body(ImportJobResponse.from(importService.get(jobId)));
    }

    @PostMapping("/{jobId}/cancel")
    public ResponseEntity<ImportJobResponse> cancel(@PathVariable String jobId) {
        importService.cancel(jobId);
        return ResponseEntity.accepted().body(ImportJobResponse.from(importService.get(jobId)));
    }

    @GetMapping("/search")
    public ResponseEntity<ImportSearchResponse> search(
            @CookieValue(value = "access_token", required = false) String token,
            @RequestParam("q") String query) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ImportSearchResponse(List.of()));
        }
        return ResponseEntity.ok(new ImportSearchResponse(importService.search(token, query)));
    }
}

package com.example.plusproject.collection.controller;

import com.example.plusproject.openapi.service.OpenApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CollectionController {

    private final OpenApiService openApiService;

    @GetMapping("/collection-openapi")
    public ResponseEntity<String> fetchFromOpenApi() {
        String result = openApiService.fetchAndSaveOpenApiData();
        return ResponseEntity.ok(result);
    }
}

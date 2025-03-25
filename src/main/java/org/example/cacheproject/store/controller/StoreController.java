package org.example.cacheproject.store.controller;

import lombok.RequiredArgsConstructor;
import org.example.cacheproject.store.dto.response.StoreResponsDto;
import org.example.cacheproject.store.service.StoreService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/stores/top-ten")
    public ResponseEntity<List<StoreResponsDto>> getTopTenStores(
            @RequestParam(required = false) Integer score,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(storeService.findTopTenStores(score, status));
    }

    @GetMapping("/stores/paging")
    public ResponseEntity<Page<StoreResponsDto>> getStores(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Integer score,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(storeService.findAllStores(page, size, score, status));
    }
}

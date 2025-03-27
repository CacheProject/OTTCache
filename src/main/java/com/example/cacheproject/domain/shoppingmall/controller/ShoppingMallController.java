package com.example.cacheproject.domain.shoppingmall.controller;

import com.example.cacheproject.common.response.PageResponse;
import com.example.cacheproject.domain.shoppingmall.entity.ShoppingMall;
import com.example.cacheproject.domain.shoppingmall.service.ShoppingMallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ShoppingMallController {

    private final ShoppingMallService shoppingMallService;

    // v1 API: 캐시 없음
    @GetMapping("/api/v1/boards/search")
    public ResponseEntity<PageResponse<ShoppingMall>> searchShoppingMalls(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<ShoppingMall> result = shoppingMallService.searchShoppingMallsByCategoryV1(keyword, page, size);

        return ResponseEntity.ok(result);
    }

    // v1 API: 인기 검색어 조회 API (DB 조회)
    @GetMapping("/popular")
    public ResponseEntity<List<String>> getPopularKeywords() {
        List<String> popularKeywords = shoppingMallService.getPopularKeywords();
        return ResponseEntity.ok(popularKeywords);
    }

    // v2 API: 캐시 적용
    @GetMapping("/api/v2/boards/search")
    public ResponseEntity searchShoppingMallsV2(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse result = shoppingMallService.searchShoppingMallsByCategoryV2(keyword, page, size);

        return ResponseEntity.ok(result);
    }

    // v2 API: 캐시 적용된 인기 검색어 조회
    @GetMapping("/api/v2/popular")
    public ResponseEntity<List<String>> getPopularKeywordsV2() {
        List<String> popularKeywords = shoppingMallService.getPopularKeywordsV2();
        return ResponseEntity.ok(popularKeywords);
    }

}

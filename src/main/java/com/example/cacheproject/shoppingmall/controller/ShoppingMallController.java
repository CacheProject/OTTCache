package com.example.cacheproject.shoppingmall.controller;

import com.example.cacheproject.response.PageResponse;
import com.example.cacheproject.shoppingmall.entity.ShoppingMall;
import com.example.cacheproject.shoppingmall.service.ShoppingMallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    // v2 API: 캐시 적용
    @GetMapping("/api/v2/boards/search")
    @Cacheable(value = "shoppingMalls", key = "#keyword + #page")
    public ResponseEntity<PageResponse<ShoppingMall>> searchShoppingMallsV2(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<ShoppingMall> result = shoppingMallService.searchShoppingMallsByCategoryV2(keyword, page, size);

        return ResponseEntity.ok(result);
    }
}

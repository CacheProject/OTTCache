package com.example.cacheproject.domain.store.controller;

import lombok.RequiredArgsConstructor;
import com.example.cacheproject.domain.store.dto.response.StoreResponsDto;
import com.example.cacheproject.domain.store.service.StoreService;
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

    /* 전체평가 필터와 업소상태 필터(2개 필터 동시적용, 각각 1개씩 적용)를 적용 후 상위 10개만 조회(모니터링 날짜기준 내림차순 정렬)*/
    @GetMapping("/stores/top-ten")
    public ResponseEntity<List<StoreResponsDto>> getTopTenStores(
            @RequestParam(required = false) Integer score,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(storeService.findTopTenStores(score, status));
    }

    /* 전체평가 필터와 업소상태 필터(2개 필터 동시적용) - 페이징 조회 */
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

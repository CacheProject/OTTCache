package com.example.cacheproject.domain.shoppingmall.service;

import com.example.cacheproject.domain.popularkeyword.entity.PopularKeyword;
import com.example.cacheproject.domain.popularkeyword.repository.PopularKeywordRepository;
import com.example.cacheproject.common.response.PageResponse;
import com.example.cacheproject.domain.shoppingmall.entity.ShoppingMall;
import com.example.cacheproject.domain.shoppingmall.repository.ShoppingMallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingMallService {

    private final ShoppingMallRepository shoppingMallRepository;
    private final PopularKeywordRepository popularKeywordRepository;

    // v1 API: 캐시 없음 + 인기 검색어 저장 (DB 활용)
    @Transactional
    public PageResponse<ShoppingMall> searchShoppingMallsByCategoryV1(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ShoppingMall> shoppingMallPage = shoppingMallRepository.findByMainProductCategoryContainingIgnoreCase(keyword, pageable);

        savePopularKeyword(keyword);

        return new PageResponse<>(
                shoppingMallPage.getContent(),
                shoppingMallPage.getNumber(),
                shoppingMallPage.getSize(),
                shoppingMallPage.getTotalPages(),
                shoppingMallPage.getTotalElements()
        );
    }

    // v1 API: 인기 검색어 저장 (DB)
    @Transactional
    public void savePopularKeyword(String keyword) {
        popularKeywordRepository.findByKeyword(keyword)
                .ifPresentOrElse(
                        PopularKeyword::incrementCount,
                        () -> popularKeywordRepository.save(new PopularKeyword(keyword))
                );
    }

    // v1 API: 인기 검색어 조회 API (DB)
    @Transactional(readOnly = true)
    public List<String> getPopularKeywords() {
        return popularKeywordRepository.findTop10ByOrderBySearchCountDesc()
                .stream()
                .map(PopularKeyword::getKeyword)
                .collect(Collectors.toList());
    }
}
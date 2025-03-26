package com.example.cacheproject.shoppingmall.service;

import com.example.cacheproject.response.PageResponse;
import com.example.cacheproject.shoppingmall.entity.ShoppingMall;
import com.example.cacheproject.shoppingmall.repository.ShoppingMallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingMallService {

    private final ShoppingMallRepository shoppingMallRepository;

    // v1 API: 캐시 없음
    @Transactional(readOnly = true)
    public PageResponse<ShoppingMall> searchShoppingMallsByCategoryV1(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ShoppingMall> shoppingMallPage = shoppingMallRepository.findByMainProductCategoryContainingIgnoreCase(keyword, pageable);

        return new PageResponse<>(
                shoppingMallPage.getContent(),
                shoppingMallPage.getNumber(),
                shoppingMallPage.getSize(),
                shoppingMallPage.getTotalPages(),
                shoppingMallPage.getTotalElements()
        );
    }

    // v2 API: 캐시 적용
    @Transactional(readOnly = true)
    @Cacheable(value = "shoppingMalls", key = "#keyword + #page")
    public PageResponse<ShoppingMall> searchShoppingMallsByCategoryV2(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ShoppingMall> shoppingMallPage = shoppingMallRepository.findByMainProductCategoryContainingIgnoreCase(keyword, pageable);

        return new PageResponse<>(
                shoppingMallPage.getContent(),
                shoppingMallPage.getNumber(),
                shoppingMallPage.getSize(),
                shoppingMallPage.getTotalPages(),
                shoppingMallPage.getTotalElements()
        );
    }
}
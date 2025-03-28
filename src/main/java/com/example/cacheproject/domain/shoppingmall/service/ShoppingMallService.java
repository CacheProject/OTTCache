package com.example.cacheproject.domain.shoppingmall.service;

import com.example.cacheproject.domain.popularkeyword.entity.PopularKeyword;
import com.example.cacheproject.domain.popularkeyword.repository.PopularKeywordRepository;
import com.example.cacheproject.common.response.PageResponse;
import com.example.cacheproject.domain.shoppingmall.entity.ShoppingMall;
import com.example.cacheproject.domain.shoppingmall.repository.ShoppingMallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingMallService {

    private final ShoppingMallRepository shoppingMallRepository;
    private final PopularKeywordRepository popularKeywordRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String POPULAR_KEYWORDS_KEY = "popularKeywords";
    private static final long CACHE_EXPIRATION_SECONDS = 3600; // 1 hour cache expiration

    // 캐시 무효화 메서드 추가
    @CacheEvict(value = "popularKeywords", key = "'top10'")
    public void refreshPopularKeywordsCache() {
        log.info("Popular Keywords Cache Refreshed");
    }

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

    // v2: 인기 검색어 캐시 적용
    @Transactional(readOnly = true)
    @Cacheable(value = "popularKeywords", key = "'top10'")
    public List<String> getPopularKeywordsV2() {
        log.info(">>> DB에서 인기 검색어 실제 조회 <<< ");
        return popularKeywordRepository.findTop10ByOrderBySearchCountDesc()
                .stream()
                .map(PopularKeyword::getKeyword)
                .collect(Collectors.toList());
    }

    // v2 API: 검색할 때마다 인기 검색어 저장 및 캐시 리프레시
    @Transactional
    public PageResponse searchShoppingMallsByCategoryV2(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page shoppingMallPage = shoppingMallRepository.findByMainProductCategoryContainingIgnoreCase(keyword, pageable);

        // 인기 검색어 저장
        savePopularKeyword(keyword);

        // 캐시 리프레시
        refreshPopularKeywordsCache();

        return new PageResponse<>(
                shoppingMallPage.getContent(),
                shoppingMallPage.getNumber(),
                shoppingMallPage.getSize(),
                shoppingMallPage.getTotalPages(),
                shoppingMallPage.getTotalElements()
        );
    }

    // v3: Redis 캐시를 활용한 검색
    @Transactional(readOnly = true)
    public PageResponse searchShoppingMallsByCategoryV3(String keyword, int page, int size) {
        log.info("🔍 검색 상세정보 - 키워드: {}, 페이지: {}, 페이지 크기: {}", keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);

        // DB에서 검색 결과 가져오기
        Page<ShoppingMall> shoppingMallPage = shoppingMallRepository.searchByCategoryOrCompanyName(keyword, pageable);

        log.info("🔢 검색 결과 그리기:");
        log.info("총 데이터 수: {}", shoppingMallPage.getTotalElements());
        log.info("현재 페이지 데이터 수: {}", shoppingMallPage.getNumberOfElements());
        shoppingMallPage.getContent().forEach(mall -> log.info("📝 매칭 데이터 - 회사명: {}, 카테고리: {}", mall.getCompanyName(), mall.getMainProductCategory()));

        // 캐시 삭제 (검색 결과를 새로 가져오기 전에 캐시를 삭제)
        String cacheKey = "search:" + keyword + ":" + page;
        redisTemplate.delete(cacheKey);

        // 검색 결과 반환
        log.info("🔢 검색 결과 - 총 데이터 수: {}, 현재 페이지 데이터 수: {}", shoppingMallPage.getTotalElements(), shoppingMallPage.getNumberOfElements());

        return new PageResponse<>(
                shoppingMallPage.getContent(),
                shoppingMallPage.getNumber(),
                shoppingMallPage.getSize(),
                shoppingMallPage.getTotalPages(),
                shoppingMallPage.getTotalElements()
        );
    }

    // Redis 캐시 정책 재검토
    private PageResponse getCachedOrFreshResult(String keyword, int page, int size) {
        String cacheKey = "search:" + keyword + ":" + page;

        // 캐시에서 데이터를 찾기
        PageResponse cachedResult = (PageResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            log.info("[캐시 조회] 캐시에서 검색 결과 가져오기");
            return cachedResult;
        }

        // 캐시가 없으면 DB에서 새로 검색하여 캐시 저장
        Pageable pageable = PageRequest.of(page, size);
        Page<ShoppingMall> shoppingMallPage = shoppingMallRepository.searchByCategoryOrCompanyName(keyword, pageable);

        // 검색 결과를 PageResponse로 포장
        PageResponse result = new PageResponse<>(
                shoppingMallPage.getContent(),
                shoppingMallPage.getNumber(),
                shoppingMallPage.getSize(),
                shoppingMallPage.getTotalPages(),
                shoppingMallPage.getTotalElements()
        );

        // 즉시 캐시 저장 (10분 동안 유지)
        redisTemplate.opsForValue().set(cacheKey, result, Duration.ofMinutes(10));

        log.info("[캐시 저장] 검색 결과 Redis에 저장");

        return result;
    }

    // v3 API: Redis를 활용한 인기 검색어 조회
    public List<String> getPopularKeywordsWithRedis() {
        // 인기 검색어를 DB에서 조회
        List<String> keywords = popularKeywordRepository.findTop10ByOrderBySearchCountDesc()
                .stream()
                .map(PopularKeyword::getKeyword)
                .collect(Collectors.toList());

        // Redis에 저장(1시간 유지)
        redisTemplate.opsForValue().set(POPULAR_KEYWORDS_KEY, keywords, Duration.ofSeconds(CACHE_EXPIRATION_SECONDS));
        log.info("[캐시 저장] 인기 검색어 Redis에 저장");

        return keywords;
    }

}
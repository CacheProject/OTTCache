package com.example.cacheproject.domain.shoppingmall.repository;

import com.example.cacheproject.domain.shoppingmall.entity.ShoppingMall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ShoppingMallRepository extends JpaRepository<ShoppingMall, Long> {

    // main_product_category 칼럼에서 LIKE 조건으로 검색하고, Pageable을 사용하여 페이징 처리
    Page<ShoppingMall> findByMainProductCategoryContainingIgnoreCase(String keyword, Pageable pageable);
}

package com.example.cacheproject.domain.shoppingmall.repository;

import com.example.cacheproject.domain.shoppingmall.entity.ShoppingMall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ShoppingMallRepository extends JpaRepository<ShoppingMall, Long> {

    // main_product_category 칼럼에서 LIKE 조건으로 검색하고, Pageable을 사용하여 페이징 처리
    Page<ShoppingMall> findByMainProductCategoryContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("SELECT s FROM ShoppingMall s WHERE " +
            "(LOWER(s.mainProductCategory) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "s.mainProductCategory IS NOT NULL AND " +
            "LENGTH(TRIM(s.mainProductCategory)) > 0 AND " +
            "(LOCATE(LOWER(:keyword), LOWER(s.mainProductCategory)) > 0 OR " +
            "LOCATE(LOWER(:keyword), LOWER(s.companyName)) > 0)")
    Page<ShoppingMall> searchByCategoryOrCompanyName(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}

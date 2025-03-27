package com.example.cacheproject.shoppingmall.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "seoul_city_internet_shopping_mall_status")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingMall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name")
    private String companyName; // 쇼핑몰 회사명

    @Column(name = "store_name")
    private String storeName; // 쇼핑몰명

    @Column(name = "domain_name", length = 1024)
    private String domainName; // 도메인명

    @Column(name = "phone_number")
    private String phoneNumber; // 전화번호

    @Column(name = "operator_email")
    private String operatorEmail; // 이메일

    @Column(name = "company_address")
    private String companyAddress; // 회사주소

    @Column(name = "store_status")
    private String storeStatus; // 업소상태

    @Column(name = "total_evaluation")
    private String totalEvaluation; // 전체평가

    @Column(name = "main_product_category")
    private String mainProductCategory; // 주요 제품 카테고리

    @Column(name = "monitoring_date")
    private String monitoringDate; // 모니터링날짜
}

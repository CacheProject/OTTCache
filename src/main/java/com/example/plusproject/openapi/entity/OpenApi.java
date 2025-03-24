package com.example.plusproject.openapi.entity;

import com.example.plusproject.common.BusinessStatus;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "from_openapi")
public class OpenApi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;                   // 상호명
    private String storeName;                     // 쇼핑몰명
    private String domainName;                    // 도메인명
    private String phoneNumber;                   // 전화번호
    private String operatorEmail;                 // 운영자이메일
    private int overallEvaluation;                // 전체평가
    private BusinessStatus businessStatus;        // 영업형태

    public OpenApi(String companyName, String storeName, String domainName, String phoneNumber, String operatorEmail, int overallEvaluation, String businessStatus) {
        this.companyName = companyName;
        this.storeName = storeName;
        this.domainName = domainName;
        this.phoneNumber = phoneNumber;
        this.operatorEmail = operatorEmail;
        this.overallEvaluation = overallEvaluation;
        this.businessStatus = BusinessStatus.fromString(businessStatus);
    }
}

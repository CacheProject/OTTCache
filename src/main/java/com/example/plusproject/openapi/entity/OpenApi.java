package com.example.plusproject.openapi.entity;

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

    @Column(length = 512)
    private String domainName;                    // 도메인명

    private String phoneNumber;                   // 전화번호
    private String operatorEmail;                 // 운영자이메일
    private String companyAddress;                // 회사 주소
    private int overallEvaluation;                // 전체평가
    private String businessStatus;                // 영업형태

    public OpenApi(String companyName, String storeName, String domainName, String phoneNumber, String operatorEmail, String companyAddress, int overallEvaluation, String businessStatus) {
        this.companyName = companyName;
        this.storeName = storeName;
        this.domainName = domainName;
        this.phoneNumber = phoneNumber;
        this.operatorEmail = operatorEmail;
        this.companyAddress = companyAddress;
        this.overallEvaluation = overallEvaluation;
        this.businessStatus = businessStatus;
    }
}

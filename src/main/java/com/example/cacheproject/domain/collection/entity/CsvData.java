package com.example.cacheproject.domain.collection.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "from_csv")
public class CsvData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;                   // 상호명
    private String storeName;                     // 쇼핑몰명

    @Column(length = 1024)
    private String domainName;                    // 도메인명

    private String phoneNumber;                   // 전화번호
    private String operatorEmail;                 // 운영자이메일
    private String companyAddress;                // 회사 주소
    private int totalEvaluation;;                // 전체평가
    private String storeStatus;;                // 영업형태
    private String mainProductCategory;
    private String monitoringDate;

    public CsvData(String companyName, String storeName, String domainName, String phoneNumber, String operatorEmail, String companyAddress, int totalEvaluation, String storeStatus, String mainProductCategory, String monitoringDate) {
        this.companyName = companyName;
        this.storeName = storeName;
        this.domainName = domainName;
        this.phoneNumber = phoneNumber;
        this.operatorEmail = operatorEmail;
        this.companyAddress = companyAddress;
        this.totalEvaluation = totalEvaluation;
        this.storeStatus = storeStatus;
        this.mainProductCategory = mainProductCategory;
        this.monitoringDate = monitoringDate;
    }

    @Override
    public String toString() {
        return "OpenApi{" +
                "id=" + id +
                ", company='" + companyName + '\'' +
                ", shopName='" + storeName + '\'' +
                ", domainName='" + domainName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", operatorEmail='" + operatorEmail + '\'' +
                ", companyAddress='" + companyAddress + '\'' +
                ", totalEvaluation=" + totalEvaluation +
                ", storeStatus='" + storeStatus + '\'' +
                ", mainProductCategory='" + mainProductCategory + '\'' +
                ", monitoringDate='" + monitoringDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CsvData csvData = (CsvData) o;
        return totalEvaluation == csvData.totalEvaluation &&
                Objects.equals(companyName, csvData.companyName) &&
                Objects.equals(storeName, csvData.storeName) &&
                Objects.equals(domainName, csvData.domainName) &&
                Objects.equals(phoneNumber, csvData.phoneNumber) &&
                Objects.equals(operatorEmail, csvData.operatorEmail) &&
                Objects.equals(companyAddress, csvData.companyAddress) &&
                Objects.equals(storeStatus, csvData.storeStatus) &&
                Objects.equals(mainProductCategory, csvData.mainProductCategory) &&
                Objects.equals(monitoringDate, csvData.monitoringDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName, storeName, domainName, phoneNumber, operatorEmail, companyAddress, totalEvaluation, storeStatus, mainProductCategory, monitoringDate);
    }
}
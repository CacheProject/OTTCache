package com.example.cacheproject.domain.collection.dto;

import com.example.cacheproject.domain.collection.entity.CsvData;
import lombok.Getter;

@Getter
public class CsvDataResponseDto {

    private Long id;
    private String companyName;
    private String storeName;
    private String domainName;
    private String phoneNumber;
    private String operatorEmail;
    private String companyAddress;
    private int totalEvaluation;
    private String storeStatus;

    public CsvDataResponseDto(Long id, String companyName, String storeName, String domainName, String phoneNumber, String operatorEmail, String companyAddress, int totalEvaluation, String storeStatus) {
        this.id = id;
        this.companyName = companyName;
        this.storeName = storeName;
        this.domainName = domainName;
        this.phoneNumber = phoneNumber;
        this.operatorEmail = operatorEmail;
        this.companyAddress = companyAddress;
        this.totalEvaluation = totalEvaluation;
        this.storeStatus = storeStatus;
    }

    public static CsvDataResponseDto toDto(CsvData csvData) {
        return new CsvDataResponseDto(
                csvData.getId(),
                csvData.getCompanyName(),
                csvData.getStoreName(),
                csvData.getDomainName(),
                csvData.getPhoneNumber(),
                csvData.getOperatorEmail(),
                csvData.getCompanyAddress(),
                csvData.getTotalEvaluation(),
                csvData.getStoreStatus()
        );
    }
}

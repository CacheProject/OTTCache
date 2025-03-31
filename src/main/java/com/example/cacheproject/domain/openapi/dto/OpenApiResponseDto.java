package com.example.cacheproject.domain.openapi.dto;

import com.example.cacheproject.domain.openapi.entity.OpenApi;
import lombok.Getter;

@Getter
public class OpenApiResponseDto {
    private Long id;
    private String companyName;
    private String storeName;
    private String domainName;
    private String phoneNumber;
    private String operatorEmail;
    private String companyAddress;
    private int totalEvaluation;
    private String storeStatus;

    public OpenApiResponseDto(Long id, String companyName, String storeName, String domainName, String phoneNumber, String operatorEmail, String companyAddress, int totalEvaluation, String storeStatus) {
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

    public static OpenApiResponseDto toDto(OpenApi openApi) {
        return new OpenApiResponseDto(
                openApi.getId(),
                openApi.getCompanyName(),
                openApi.getStoreName(),
                openApi.getDomainName(),
                openApi.getPhoneNumber(),
                openApi.getOperatorEmail(),
                openApi.getCompanyAddress(),
                openApi.getTotalEvaluation(),
                openApi.getStoreStatus()
        );
    }
}

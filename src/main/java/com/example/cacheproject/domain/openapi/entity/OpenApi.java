package com.example.cacheproject.domain.openapi.entity;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "from_openapi")
@XmlRootElement(name = "row")
@XmlAccessorType(XmlAccessType.FIELD)
public class OpenApi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @XmlElement(name = "COMPANY")
    private String companyName;

    @XmlElement(name = "SHOP_NAME")
    private String storeName;

    @Column(length = 1024)
    @XmlElement(name = "DOMAIN_NAME")
    private String domainName;

    @XmlElement(name = "TEL")
    private String phoneNumber;

    @XmlElement(name = "EMAIL")
    private String operatorEmail;

    @XmlElement(name = "COM_ADDR")
    private String companyAddress;

    @XmlElement(name = "TOT_RATINGPOINT")
    private int totalEvaluation;

    @XmlElement(name = "STAT_NM")
    private String storeStatus;

    @XmlElement(name = "SERVICE")
    private String mainProductCategory; // 주요 제품 카테고리

    @XmlElement(name = "REG_DATE")
    private String monitoringDate; // 모니터링날짜

    public OpenApi(String companyName, String storeName, String domainName, String phoneNumber, String operatorEmail, String companyAddress, int totalEvaluation, String storeStatus, String mainProductCategory, String monitoringDate) {
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
                ", overallEvaluation=" + totalEvaluation +
                ", status='" + storeStatus + '\'' +
                ", mainProductCategory='" + mainProductCategory + '\'' +
                ", monitoringDate='" + monitoringDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenApi openApi = (OpenApi) o;
        return totalEvaluation == openApi.totalEvaluation &&
                Objects.equals(companyName, openApi.companyName) &&
                Objects.equals(storeName, openApi.storeName) &&
                Objects.equals(domainName, openApi.domainName) &&
                Objects.equals(phoneNumber, openApi.phoneNumber) &&
                Objects.equals(operatorEmail, openApi.operatorEmail) &&
                Objects.equals(companyAddress, openApi.companyAddress) &&
                Objects.equals(storeStatus, openApi.storeStatus) &&
                Objects.equals(mainProductCategory, openApi.mainProductCategory) &&
                Objects.equals(monitoringDate, openApi.monitoringDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName, storeName, domainName, phoneNumber, operatorEmail, companyAddress, totalEvaluation, storeStatus);
    }
}

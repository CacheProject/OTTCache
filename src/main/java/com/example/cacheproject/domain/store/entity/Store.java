package com.example.cacheproject.domain.store.entity;

import com.example.cacheproject.domain.store.dto.request.StoreRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "stores")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;

    private String storeName;

    private String domainName;
    private String phoneNumber;
    private String operatorEmail;
    private String companyAddress;

    private String storeStatus;

    private String totalEvaluation;
    private String mainProductCategory;
    private String monitoringDate;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public Store(StoreRequestDto dto, Long userId) {
        this.storeName = dto.getStoreName();
        this.operatorEmail = dto.getEmail();
        this.userId = userId;
    }
}

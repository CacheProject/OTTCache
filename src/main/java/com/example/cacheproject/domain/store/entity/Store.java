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

    private String mutaul;

    private String store_name;

    private String domain_name;

    private String email;

    private String store_status;

    private String open_status;

    private String total_evalution;

    private String monitoring_date;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public Store(StoreRequestDto dto, Long userId) {
        this.store_name = dto.getStoreName();
        this.email = dto.getEmail();
        this.userId = userId;
    }
}

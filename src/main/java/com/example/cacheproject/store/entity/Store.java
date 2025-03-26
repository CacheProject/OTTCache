package com.example.cacheproject.store.entity;

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
}

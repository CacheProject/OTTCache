package com.example.plusproject.openapi.repository;

import com.example.plusproject.openapi.entity.OpenApi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpenApiRepository extends JpaRepository<OpenApi, Long> {
}

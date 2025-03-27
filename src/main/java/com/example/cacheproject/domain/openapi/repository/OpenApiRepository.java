package com.example.cacheproject.domain.openapi.repository;

import com.example.cacheproject.domain.openapi.entity.OpenApi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpenApiRepository extends JpaRepository<OpenApi, Long> {
}

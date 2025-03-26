package com.example.cacheproject.openapi.repository;

import com.example.cacheproject.openapi.entity.OpenApi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpenApiRepository extends JpaRepository<OpenApi, Long> {
}

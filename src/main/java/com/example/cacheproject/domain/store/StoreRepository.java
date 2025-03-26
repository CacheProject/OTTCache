package com.example.cacheproject.domain.store;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}

package com.example.cacheproject.domain.popularkeyword.repository;

import com.example.cacheproject.domain.popularkeyword.entity.PopularKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PopularKeywordRepository extends JpaRepository<PopularKeyword, Long> {
    Optional<PopularKeyword> findByKeyword(String keyword);
    List<PopularKeyword> findTop10ByOrderBySearchCountDesc();
}

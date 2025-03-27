package com.example.cacheproject.domain.popularkeyword.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "popular_keyword")
public class PopularKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String keyword;

    @Column(nullable = false)
    private Long searchCount = 0L;

    public PopularKeyword(String keyword) {
        this.keyword = keyword;
        this.searchCount = 1L;
    }

    public void incrementCount() {
        this.searchCount++;
    }
}


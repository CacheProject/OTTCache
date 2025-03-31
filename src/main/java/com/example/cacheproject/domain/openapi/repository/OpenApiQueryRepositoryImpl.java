package com.example.cacheproject.domain.openapi.repository;

import com.example.cacheproject.domain.openapi.entity.OpenApi;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.cacheproject.domain.openapi.entity.QOpenApi.openApi;

@Repository
@RequiredArgsConstructor
public class OpenApiQueryRepositoryImpl implements OpenApiQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<OpenApi> findAllOpenApiByCursor (Integer score, String status, Long lastPageId, int size) {
        BooleanBuilder builder = new BooleanBuilder();

        if (score != null) {
            builder.and(openApi.totalEvaluation.eq(score));
        }
        if (status != null) {
            builder.and(openApi.storeStatus.eq(status));
        }
        if (lastPageId != null) {
            builder.and(openApi.id.lt(lastPageId));
        }
        return queryFactory
                .selectFrom(openApi)
                .where(builder)
                .orderBy(openApi.id.desc())
                .limit(size + 1)
                .fetch();
    }
}

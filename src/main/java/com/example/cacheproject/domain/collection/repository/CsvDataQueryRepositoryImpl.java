package com.example.cacheproject.domain.collection.repository;

import com.example.cacheproject.domain.collection.entity.CsvData;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.cacheproject.domain.collection.entity.QCsvData.csvData;

@Repository
@RequiredArgsConstructor
public class CsvDataQueryRepositoryImpl implements CsvDataQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CsvData> findAllCsvDataByCursor (Integer score, String status, Long lastPageId, int size) {
        BooleanBuilder builder = new BooleanBuilder();

        if (score != null) {
            builder.and(csvData.totalEvaluation.eq(score));
        }
        if (status != null) {
            builder.and(csvData.storeStatus.eq(status));
        }
        if (lastPageId != null) {
            builder.and(csvData.id.lt(lastPageId));
        }
        return queryFactory
                .selectFrom(csvData)
                .where(builder)
                .orderBy(csvData.id.desc())
                .limit(size + 1)
                .fetch();
    }
}

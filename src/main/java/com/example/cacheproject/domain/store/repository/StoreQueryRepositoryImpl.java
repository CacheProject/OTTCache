package com.example.cacheproject.domain.store.repository;

import com.example.cacheproject.domain.store.entity.Store;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.cacheproject.domain.store.entity.QStore.store;

@Repository
@RequiredArgsConstructor
public class StoreQueryRepositoryImpl implements StoreQueryRepository {

    private final JPAQueryFactory queryFactory;

    // 전체평가 필터만 적용 후 상위 10개만 조회(모니터링 날짜기준 내림차순 정렬)하는 쿼리
    @Override
    public List<Store> findTop10ByTotal_evaluationOrderByMonitoring_dateDesc (Integer score) {
        return queryFactory
                .selectFrom(store)
                .where(store.total_evaluation.eq(String.valueOf(score)))
                .orderBy(store.monitoring_date.desc())
                .limit(10)
                .fetch();
    }

    // 업소상태 필터만 적용 후 상위 10개만 조회(모니터링 날짜기준 내림차순 정렬)하는 쿼리
    @Override
    public List<Store> findTop10ByOpen_statusOrderByMonitoring_dateDesc (String status) {
        return queryFactory
                .selectFrom(store)
                .where(store.open_status.eq(status))
                .orderBy(store.monitoring_date.desc())
                .limit(10)
                .fetch();
    }

    // 전체평가 필터와 업소상태 필터를 동시에 적용 후 상위 10개만 조회(모니터링 날짜기준 내림차순 정렬)하는 쿼리
    @Override
    public List<Store> findTop10ByTotal_evaluationAndOpen_statusOrderByMonitoring_dateDesc (Integer score, String status) {
        return queryFactory
                .selectFrom(store)
                .where(store.total_evaluation.eq(String.valueOf(score)),
                        store.open_status.eq(status))
                .orderBy(store.monitoring_date.desc())
                .limit(10)
                .fetch();
    }

    // 전체평가 필터와 업소상태 필터를 동시에 적용시키는 쿼리
    @Override
    public Page<Store> findAllStoresTotal_evaluationAndOpen_status (Pageable pageable,Integer score, String status) {
//        pageable = pageable == null ? PageRequest.of(0, 10) : pageable;
        List<Store> storeList =  queryFactory
                .selectFrom(store)
                .where(store.total_evaluation.eq(String.valueOf(score)),
                        store.open_status.eq(status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long count = queryFactory
                .selectFrom(store)
                .where(store.total_evaluation.eq(String.valueOf(score)),
                        store.open_status.eq(status))
                .fetchCount();
        return new PageImpl<>(storeList, pageable, count);
    }

    // cursor 기반 페이지네이션
    @Override
    public List<Store> findAllStoresByCursorTotal_evaluationAndOpen_status (Integer score, String status, Long lastPageId, int size) {
        BooleanBuilder builder = new BooleanBuilder();

        if (score != null) {
            builder.and(store.total_evaluation.eq(String.valueOf(score)));
        }
        if (status != null) {
            builder.and(store.open_status.eq(status));
        }
        if (lastPageId != null) {
            builder.and(store.id.lt(lastPageId));
        }

        return queryFactory
                .selectFrom(store)
                .where(builder)
                .orderBy(store.id.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public void deleteByUserId(Long userId) {
        queryFactory
                .delete(store)
                .where(store.userId.eq(userId))
                .execute();
    }

//    @Override
//    public Optional<Store> findByUserId(Long userId) {
//        Store storeEntity = queryFactory
//                .selectFrom(store)
//                .where(store.user.id.eq(userId))
//                .fetchOne();
//        return Optional.empty();
//    }
}

package com.example.cacheproject.common.util;

import jakarta.persistence.EntityManager;

import java.util.List;

public class BatchInsertUtil {

    private static final int BATCH_SIZE = 100; // 100개 단위로 처리

    public static <T> void batchInsert(EntityManager entityManager, List<T> entities) {
        for (int i = 0; i < entities.size(); i++) {
            entityManager.persist(entities.get(i));

            // 배치 사이즈마다 flush 및 clear 실행하여 성능 최적화
            if (i > 0 && i % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush(); // 마지막 남은 데이터까지 flush
        entityManager.clear();
    }
}

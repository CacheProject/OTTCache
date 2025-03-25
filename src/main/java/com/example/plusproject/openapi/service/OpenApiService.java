package com.example.plusproject.openapi.service;

import com.example.plusproject.common.util.DataConsistencyUtil;
import com.example.plusproject.openapi.entity.OpenApi;
import com.example.plusproject.openapi.fetchstatus.entity.OpenApiFetchStatus;
import com.example.plusproject.openapi.fetchstatus.repository.OpenApiFetchStatusRepository;
import com.example.plusproject.openapi.repository.OpenApiRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class OpenApiService {

    private static final int BATCH_SIZE = 10_000; // 한 번에 가져올 데이터 개수
    private static final int MAX_REQUEST_SIZE = 1000; // OpenAPI에서 한 번에 요청할 수 있는 최대 데이터 건수
    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;
    private final OpenApiRepository openApiRepository;
    private final OpenApiFetchStatusRepository openApiFetchStatusRepository;
    private final DataConsistencyUtil dataConsistencyUtil;
    private final String openApiUrl;


    @PersistenceContext
    private EntityManager entityManager;

    public OpenApiService(RestTemplate restTemplate, XmlMapper xmlMapper,
                          OpenApiRepository openApiRepository,
                          OpenApiFetchStatusRepository openApiFetchStatusRepository,
                          DataConsistencyUtil dataConsistencyUtil,
                          @Value("${openapi.url}") String openApiUrl) {
        this.restTemplate = restTemplate;
        this.xmlMapper = xmlMapper;
        this.openApiRepository = openApiRepository;
        this.openApiFetchStatusRepository = openApiFetchStatusRepository;
        this.dataConsistencyUtil = dataConsistencyUtil;
        this.openApiUrl = openApiUrl;
    }

    @Transactional
    public String fetchAndSaveOpenApiData() {
        try {
            int startRow = 1;
            int endRow = startRow + MAX_REQUEST_SIZE - 1; // 1000개씩 요청하도록 설정
            int totalInserted = 0; // 총 삽입된 데이터 개수를 카운트

            // 마지막으로 삽입된 행을 가져오기
            OpenApiFetchStatus fetchStatus = openApiFetchStatusRepository.findTopByOrderByUpdatedAtDesc();
            if (fetchStatus != null) {
                startRow = fetchStatus.getLastFetchedRow() + 1;
                endRow = startRow + MAX_REQUEST_SIZE - 1;
            }

            // BATCH_SIZE만큼 데이터 삽입
            while (totalInserted < BATCH_SIZE) {
                // API 호출 URL 생성
                String apiUrlWithParams = openApiUrl + "/" + startRow + "/" + endRow;
                ResponseEntity<String> response = restTemplate.getForEntity(apiUrlWithParams, String.class);
                String responseBody = response.getBody();

                // 출력 로그
                // log.info("OpenAPI Response: {}", responseBody);

                // XML 데이터를 JSON으로 변환
                JsonNode rootNode = xmlMapper.readTree(responseBody);
                JsonNode dataList = rootNode.path("row");

                // 데이터가 없으면 종료
                if (dataList.isEmpty()) {
                    return "더 이상 데이터가 없습니다.";
                }

                List<OpenApi> batchList = new ArrayList<>();
                for (JsonNode node : dataList) {
                    OpenApi openApi = new OpenApi(
                            node.path("COMPANY").asText(),
                            node.path("SHOP_NAME").asText(),
                            node.path("DOMAIN_NAME").asText(),
                            node.path("TEL").asText(),
                            node.path("EMAIL").asText(),
                            node.path("COM_ADDR").asText(),
                            node.path("TOT_RATINGPOINT").asInt(),
                            node.path("STAT_NM").asText()
                    );
                    batchList.add(openApi);
                }

                // 배치 단위로 데이터베이스에 삽입
                batchInsert(batchList);

                totalInserted += batchList.size(); // 삽입된 데이터 수 증가

                // 데이터 정합성 체크
                boolean isConsistent = dataConsistencyUtil.checkOpenApiDataConsistency(batchList);  // 정합성 체크
                if (!isConsistent) {
                    throw new RuntimeException("데이터 정합성 체크 실패! 저장된 데이터와 불러온 데이터가 일치하지 않습니다.");
                }

                // 마지막으로 삽입된 행을 추적하여 상태 업데이트
                OpenApiFetchStatus newFetchStatus = new OpenApiFetchStatus();
                newFetchStatus.setLastFetchedRow(endRow);
                openApiFetchStatusRepository.save(newFetchStatus);

                // 10000개 삽입 후 종료
                if (totalInserted >= BATCH_SIZE) {
                    break;
                }

                startRow = endRow + 1;
                endRow = startRow + MAX_REQUEST_SIZE - 1;
            }

            return "10,000개 데이터가 성공적으로 삽입되었습니다.";
        } catch (Exception e) {
            log.error("오류 발생: ", e);
            return "오류 발생: " + e.getMessage();
        }
    }

    // 100개 단위로 db에 insert
    @Transactional
    public void batchInsert(List<OpenApi> batchList) {
        int batchSize = 100;
        for (int i = 0; i < batchList.size(); i++) {
            entityManager.persist(batchList.get(i));
            if (i > 0 && i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }
}

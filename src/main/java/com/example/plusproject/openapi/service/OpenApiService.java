package com.example.plusproject.openapi.service;

import com.example.plusproject.common.util.DataConsistencyUtil;
import com.example.plusproject.exception.BadRequestException;
import com.example.plusproject.exception.DataIntegrityException;
import com.example.plusproject.openapi.entity.OpenApi;
import com.example.plusproject.openapi.fetchstatus.entity.OpenApiFetchStatus;
import com.example.plusproject.openapi.fetchstatus.repository.OpenApiFetchStatusRepository;
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
    private final OpenApiFetchStatusRepository openApiFetchStatusRepository;
    private final DataConsistencyUtil dataConsistencyUtil;
    private final String openApiUrl;


    @PersistenceContext
    private EntityManager entityManager;

    public OpenApiService(RestTemplate restTemplate, XmlMapper xmlMapper,
                          OpenApiFetchStatusRepository openApiFetchStatusRepository,
                          DataConsistencyUtil dataConsistencyUtil,
                          @Value("${openapi.url}") String openApiUrl) {
        this.restTemplate = restTemplate;
        this.xmlMapper = xmlMapper;
        this.openApiFetchStatusRepository = openApiFetchStatusRepository;
        this.dataConsistencyUtil = dataConsistencyUtil;
        this.openApiUrl = openApiUrl;
    }

    @Transactional
    public String fetchAndSaveOpenApiData() {
        int startRow = 1;
        int endRow = startRow + MAX_REQUEST_SIZE - 1;
        int totalInserted = 0;

        OpenApiFetchStatus fetchStatus = openApiFetchStatusRepository.findTopByOrderByUpdatedAtDesc();
        if (fetchStatus != null) {
            startRow = fetchStatus.getLastFetchedRow() + 1;
            endRow = startRow + MAX_REQUEST_SIZE - 1;
        }

        while (totalInserted < BATCH_SIZE) {
            String apiUrlWithParams = openApiUrl + "/" + startRow + "/" + endRow;
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrlWithParams, String.class);
            String responseBody = response.getBody();

            JsonNode rootNode;
            try {
                rootNode = xmlMapper.readTree(responseBody);
            } catch (Exception e) {
                throw new BadRequestException("XML 데이터를 파싱하는 중 오류 발생");
            }


            JsonNode dataList = rootNode.path("row");

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

            batchInsert(batchList);
            totalInserted += batchList.size();

            boolean isConsistent = dataConsistencyUtil.checkOpenApiDataConsistency(batchList);
            if (!isConsistent) {
                throw new DataIntegrityException("데이터 정합성 체크 실패! 저장된 데이터와 불러온 데이터가 일치하지 않습니다.");
            }

            OpenApiFetchStatus newFetchStatus = new OpenApiFetchStatus();
            newFetchStatus.setLastFetchedRow(endRow);
            openApiFetchStatusRepository.save(newFetchStatus);

            if (totalInserted >= BATCH_SIZE) {
                break;
            }

            startRow = endRow + 1;
            endRow = startRow + MAX_REQUEST_SIZE - 1;
        }

        return totalInserted + "개의 OpenAPI 데이터가 성공적으로 삽입되었습니다.";
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

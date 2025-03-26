package com.example.cacheproject.openapi.service;

import com.example.cacheproject.common.util.BatchInsertUtil;
import com.example.cacheproject.common.util.DataConsistencyUtil;
import com.example.cacheproject.exception.BadRequestException;
import com.example.cacheproject.exception.DataIntegrityException;
import com.example.cacheproject.openapi.entity.OpenApi;
import com.example.cacheproject.openapi.fetchstatus.entity.OpenApiFetchStatus;
import com.example.cacheproject.openapi.fetchstatus.repository.OpenApiFetchStatusRepository;
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

@Slf4j
@Service
public class OpenApiService {

    private static final int MAX_LIMIT = 10_000; // 한 번 API 호출에 최대 삽입 가능한 데이터 개수
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

    // OpenAPI 데이터를 가져와서 db에 저장하는 메서드
    @Transactional
    public String fetchAndSaveOpenApiData() {
        int startRow = 1;
        int endRow = startRow + MAX_REQUEST_SIZE - 1;
        int totalInserted = 0;

        // 가장 최근 저장된 데이터의 마지막 row 번호를 가져와서 이어서 저장하도록 설정
        OpenApiFetchStatus fetchStatus = openApiFetchStatusRepository.findTopByOrderByUpdatedAtDesc();
        if (fetchStatus != null) {
            startRow = fetchStatus.getLastFetchedRow() + 1;
            endRow = startRow + MAX_REQUEST_SIZE - 1;
        }

        while (totalInserted < MAX_LIMIT) {
            String apiUrlWithParams = openApiUrl + "/" + startRow + "/" + endRow;
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrlWithParams, String.class);
            String responseBody = response.getBody();

            JsonNode rootNode;
            try {
                rootNode = xmlMapper.readTree(responseBody); // XML 데이터를 JSON으로 변환
            } catch (Exception e) {
                throw new BadRequestException("XML 데이터를 파싱하는 중 오류 발생");
            }

            JsonNode dataList = rootNode.path("row");

            // 더 이상 불러올 데이터가 없으면 종료
            if (dataList.isEmpty()) {
                return "더 이상 데이터가 없습니다.";
            }

            List<OpenApi> batchList = new ArrayList<>();
            for (JsonNode node : dataList) {
                // OpenApi 엔티티 객체 생성 후 리스트에 추가
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

            // 데이터베이스에 배치 단위로 insert
            batchInsert(batchList);
            totalInserted += batchList.size();

            // 데이터 정합성 체크
            boolean isConsistent = dataConsistencyUtil.checkOpenApiDataConsistency(batchList);
            if (!isConsistent) {
                throw new DataIntegrityException("데이터 정합성 체크 실패! 저장된 데이터와 불러온 데이터가 일치하지 않습니다.");
            }

            // 저장된 마지막 row 값을 기록
            OpenApiFetchStatus newFetchStatus = new OpenApiFetchStatus();
            newFetchStatus.setLastFetchedRow(endRow);
            openApiFetchStatusRepository.save(newFetchStatus);

            if (totalInserted >= MAX_LIMIT) {
                break;
            }

            // 다음 요청을 위한 row 번호 갱신
            startRow = endRow + 1;
            endRow = startRow + MAX_REQUEST_SIZE - 1;
        }

        return totalInserted + "개의 OpenAPI 데이터가 성공적으로 삽입되었습니다.";
    }

    // OpenAPI 데이터를 100개 단위로 db에 insert하는 메서드
    @Transactional
    public void batchInsert(List<OpenApi> batchList) {
        BatchInsertUtil.batchInsert(entityManager, batchList);
    }
}

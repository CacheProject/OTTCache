package com.example.plusproject.openapi.service;

import com.example.plusproject.openapi.entity.OpenApi;
import com.example.plusproject.openapi.fetchstatus.entity.OpenApiFetchStatus;
import com.example.plusproject.openapi.fetchstatus.repository.OpenApiFetchStatusRepository;
import com.example.plusproject.openapi.repository.OpenApiRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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

    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;
    private final OpenApiRepository openApiRepository;
    private final OpenApiFetchStatusRepository openApiFetchStatusRepository;
    private final String openApiUrl;

    public OpenApiService(RestTemplate restTemplate, XmlMapper xmlMapper,
                          OpenApiRepository openApiRepository,
                          OpenApiFetchStatusRepository openApiFetchStatusRepository,
                          @Value("${openapi.url}") String openApiUrl) {
        this.restTemplate = restTemplate;
        this.xmlMapper = xmlMapper;
        this.openApiRepository = openApiRepository;
        this.openApiFetchStatusRepository = openApiFetchStatusRepository;
        this.openApiUrl = openApiUrl;
    }

    @Transactional
    public String fetchAndSaveOpenApiData() {
        try {
            int startRow = 1;
            OpenApiFetchStatus fetchStatus = openApiFetchStatusRepository.findTopByOrderByUpdatedAtDesc();
            if (fetchStatus != null) {
                startRow = fetchStatus.getLastFetchedRow() + 1;
            }

            int totalFetched = 0;
            List<OpenApi> batchList = new ArrayList<>();

            while (totalFetched < BATCH_SIZE) {
                int endRow = startRow + 99; // 한 번에 100개씩 호출
                String apiUrlWithParams = openApiUrl + "/" + startRow + "/" + endRow;
                ResponseEntity<String> response = restTemplate.getForEntity(apiUrlWithParams, String.class);
                String responseBody = response.getBody();
                log.info("OpenAPI Response [{} - {}]: {}", startRow, endRow, responseBody);

                JsonNode rootNode = xmlMapper.readTree(responseBody);
                JsonNode dataList = rootNode.path("row");

                if (dataList.isEmpty()) {
                    log.info("더 이상 데이터가 없습니다.");
                    break;
                }

                for (JsonNode node : dataList) {
                    String companyName = node.path("COMPANY").asText();
                    String storeName = node.path("SHOP_NAME").asText();
                    String domainName = node.path("DOMAIN_NAME").asText();
                    String phoneNumber = node.path("TEL").asText();
                    String operatorEmail = node.path("EMAIL").asText();
                    String companyAddress = node.path("COM_ADDR").asText();
                    int overallEvaluation = node.path("TOT_RATINGPOINT").asInt();
                    String businessStatus = node.path("STAT_NM").asText();

                    OpenApi openApi = new OpenApi(companyName, storeName, domainName, phoneNumber, operatorEmail, companyAddress, overallEvaluation, businessStatus);
                    batchList.add(openApi);
                }

                totalFetched += dataList.size();
                startRow = endRow + 1; // 다음 호출을 위해 시작 번호 증가

                if (totalFetched >= BATCH_SIZE) {
                    break;
                }
            }

            // 한 번에 저장
            openApiRepository.saveAll(batchList);

            // 마지막 호출한 endRow 저장
            OpenApiFetchStatus newFetchStatus = new OpenApiFetchStatus();
            newFetchStatus.setLastFetchedRow(startRow - 1);
            openApiFetchStatusRepository.save(newFetchStatus);

            // 데이터 정합성 체크
            boolean isConsistent = checkDataConsistency(batchList);
            if (!isConsistent) {
                throw new RuntimeException("데이터 정합성 체크 실패! 저장된 데이터와 불러온 데이터가 일치하지 않습니다.");
            }

            return totalFetched + "개의 OpenAPI 데이터가 성공적으로 입력되었습니다.";
        } catch (Exception e) {
            log.error("오류 발생: ", e);
            return "오류 발생: " + e.getMessage();
        }
    }


    private boolean checkDataConsistency(List<OpenApi> batchList) {
        if (batchList.isEmpty()) {
            return true; // 저장할 데이터가 없는 경우 문제 없음
        }

        Random random = new Random();
        int sampleSize = Math.min(5, batchList.size()); // 최대 5개 랜덤 샘플링

        for (int i = 0; i < sampleSize; i++) {
            OpenApi randomSample = batchList.get(random.nextInt(batchList.size()));

            Optional<OpenApi> dbRecord = openApiRepository.findById(randomSample.getId());
            if (dbRecord.isEmpty() || !dbRecord.get().equals(randomSample)) {
                log.error("정합성 체크 실패: {}", randomSample);
                return false;
            }
        }

        log.info("정합성 체크 성공: 샘플 {}개 데이터가 정상적으로 저장됨", sampleSize);
        return true;
    }
}


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

@Slf4j
@Service
public class OpenApiService {

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
            int endRow = 100;

            OpenApiFetchStatus fetchStatus = openApiFetchStatusRepository.findTopByOrderByUpdatedAtDesc();
            if (fetchStatus != null) {
                startRow = fetchStatus.getLastFetchedRow() + 1;
                endRow = startRow + 99;
            }

            String apiUrlWithParams = openApiUrl + "/" + startRow + "/" + endRow;
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrlWithParams, String.class);
            String responseBody = response.getBody();
            log.info("OpenAPI Response: {}", responseBody);

            JsonNode rootNode = xmlMapper.readTree(responseBody);
            JsonNode dataList = rootNode.path("row");

            if (dataList.isEmpty()) {
                return "더 이상 데이터가 없습니다.";
            }

            List<OpenApi> batchList = new ArrayList<>();
            for (JsonNode node : dataList) {
                String companyName = node.path("COMPANY").asText();
                String storeName = node.path("SHOP_NAME").asText();
                String domainName = node.path("DOMAIN_NAME").asText();
                String phoneNumber = node.path("TEL").asText();
                String operatorEmail = node.path("EMAIL").asText();
                int overallEvaluation = node.path("OVERALL_EVALUATION").asInt();
                String businessStatus = node.path("BUSINESS_STATUS").asText();

                OpenApi openApi = new OpenApi(companyName, storeName, domainName, phoneNumber, operatorEmail, overallEvaluation, businessStatus);
                batchList.add(openApi);
            }

            openApiRepository.saveAll(batchList);

            OpenApiFetchStatus newFetchStatus = new OpenApiFetchStatus();
            newFetchStatus.setLastFetchedRow(endRow);
            openApiFetchStatusRepository.save(newFetchStatus);

            return "100개의 OpenAPI 데이터가 성공적으로 입력되었습니다.";
        } catch (Exception e) {
            log.error("오류 발생: ", e);
            return "오류 발생: " + e.getMessage();
        }
    }
}


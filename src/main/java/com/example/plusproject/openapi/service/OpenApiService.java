package com.example.plusproject.openapi.service;

import com.example.plusproject.openapi.entity.OpenApi;
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
    private final String openApiUrl;

    public OpenApiService(RestTemplate restTemplate, XmlMapper xmlMapper,
                          OpenApiRepository openApiRepository,
                          @Value("${openapi.url}") String openApiUrl) {
        this.restTemplate = restTemplate;
        this.xmlMapper = xmlMapper;
        this.openApiRepository = openApiRepository;
        this.openApiUrl = openApiUrl;
    }

    @Transactional
    public String fetchAndSaveOpenApiData() {
        try {
            int startRow = 1;
            int endRow = 100;
            boolean hasMoreData = true;

            while (hasMoreData) {
                // OpenAPI에서 데이터 가져오기
                String url = String.format("%s?startRow=%d&endRow=%d", openApiUrl, startRow, endRow);
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                String responseBody = response.getBody();
                log.info("OpenAPI Response: {}", responseBody); // 응답 로그 출력

                // XML을 JSON으로 변환
                JsonNode rootNode = xmlMapper.readTree(responseBody);
                JsonNode dataList = rootNode.path("row");

                if (dataList.isEmpty()) {
                    hasMoreData = false;  // 데이터가 더 이상 없으면 종료
                    break;
                }

                // 데이터 리스트를 OpenApi 엔티티로 변환하여 저장
                List<OpenApi> batchList = new ArrayList<>();
                for (JsonNode node : dataList) {
                    String companyName = node.path("COMPANY").asText();
                    String storeName = node.path("SHOP_NAME").asText();
                    String domainName = node.path("DOMAIN_NAME").asText();
                    String phoneNumber = node.path("TEL").asText();
                    String operatorEmail = node.path("EMAIL").asText();
                    int overallEvaluation = node.path("OVERALL_EVALUATION").asInt();
                    String businessStatus = node.path("BUSINESS_STATUS").asText("UNKNOWN");  // 기본값 설정

                    // OpenApi 객체로 변환하여 리스트에 추가
                    OpenApi openApi = new OpenApi(companyName, storeName, domainName, phoneNumber, operatorEmail, overallEvaluation, businessStatus);
                    batchList.add(openApi);
                }

                // 데이터베이스에 일괄 저장
                openApiRepository.saveAll(batchList);
                log.info("데이터 저장 완료. {} ~ {} 행", startRow, endRow);

                // 다음 호출을 위해 startRow, endRow 값 업데이트
                startRow = endRow + 1;
                endRow = endRow + 100;
            }

            return "OpenAPI 데이터가 성공적으로 입력되었습니다.";  // 성공 메시지
        } catch (Exception e) {
            log.error("오류 발생: ", e);  // 오류 로그 기록
            return "오류 발생: " + e.getMessage();  // 오류 메시지 반환
        }
    }
}


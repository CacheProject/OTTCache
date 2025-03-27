package com.example.cacheproject.openapi.service;

import com.example.cacheproject.common.util.BatchInsertUtil;
import com.example.cacheproject.common.util.DataConsistencyUtil;
import com.example.cacheproject.exception.BadRequestException;
import com.example.cacheproject.exception.DataIntegrityException;
import com.example.cacheproject.exception.OpenApiException;
import com.example.cacheproject.openapi.dto.OpenApiResponse;
import com.example.cacheproject.openapi.entity.OpenApi;
import com.example.cacheproject.openapi.fetchstatus.entity.OpenApiFetchStatus;
import com.example.cacheproject.openapi.fetchstatus.repository.OpenApiFetchStatusRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiService {

    private static final int MAX_TOTAL_LIMIT = 10_000; // 총 최대 삽입 데이터 개수
    private static final int MAX_REQUEST_SIZE = 1000; // OpenAPI에서 한 번에 요청할 수 있는 최대 데이터 건수
    private static final int BATCH_INSERT_SIZE = 100; // DB에 한 번에 삽입할 데이터 개수

    private final RestTemplate restTemplate;
    private final OpenApiFetchStatusRepository openApiFetchStatusRepository;
    private final DataConsistencyUtil dataConsistencyUtil;

    @Value("${openapi.url}")
    private String openApiUrl;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public String fetchAndSaveOpenApiData() {
        int startRow = 1;
        int endRow = MAX_REQUEST_SIZE;
        int totalInserted = 0;

        OpenApiFetchStatus lastFetchStatus = openApiFetchStatusRepository.findTopByOrderByUpdatedAtDesc();
        if (lastFetchStatus != null) {
            startRow = lastFetchStatus.getLastFetchedRow() + 1;
            endRow = startRow + MAX_REQUEST_SIZE - 1;
        }

        while (totalInserted < MAX_TOTAL_LIMIT) {
            String apiUrlWithParams = openApiUrl + "/" + startRow + "/" + endRow;

            ResponseEntity<String> response;
            try {
                response = restTemplate.getForEntity(apiUrlWithParams, String.class);
            } catch (Exception e) {
                throw new OpenApiException("API 호출 중 오류 발생: " + e.getMessage());
            }

            // 요청 본문 null 체크
            String responseBody = response.getBody();
            if (responseBody == null) {
                throw new BadRequestException("API 응답 본문이 비어있습니다.");
            }

            // XML 파싱
            List<OpenApi> fetchedList = parseXmlResponse(responseBody);

            // 더 이상 불러올 데이터가 없으면 종료
            if (fetchedList.isEmpty()) {
                break;
            }

            // 배치 인서트 로직
            List<OpenApi> batchToInsert = new ArrayList<>();
            for (OpenApi openApi : fetchedList) {
                batchToInsert.add(openApi);

                if (batchToInsert.size() == BATCH_INSERT_SIZE) {
                    performBatchInsertWithConsistencyCheck(batchToInsert);
                    totalInserted += batchToInsert.size();
                    batchToInsert.clear();
                }
            }

            // 남은 데이터 처리
            if (!batchToInsert.isEmpty()) {
                performBatchInsertWithConsistencyCheck(batchToInsert);
                totalInserted += batchToInsert.size();
            }

            // 마지막 처리된 row 상태 저장
            saveLastFetchedStatus(endRow);

            // 총 삽입 데이터가 최대 제한을 초과하면 종료
            if (totalInserted >= MAX_TOTAL_LIMIT) {
                break;
            }

            // 다음 요청을 위한 row 번호 갱신
            startRow = endRow + 1;
            endRow = startRow + MAX_REQUEST_SIZE - 1;
        }

        return totalInserted + "개의 OpenAPI 데이터가 성공적으로 삽입되었습니다.";
    }

    private List<OpenApi> parseXmlResponse(String xmlResponse) {
        if (xmlResponse == null || xmlResponse.trim().isEmpty()) {
            throw new BadRequestException("XML 응답이 비어있습니다.");
        }

        try {
            JAXBContext context = JAXBContext.newInstance(OpenApiResponse.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            OpenApiResponse response = (OpenApiResponse) unmarshaller.unmarshal(new StringReader(xmlResponse));

            if (response == null || response.getRows() == null || response.getRows().isEmpty()) {
                throw new BadRequestException("파싱된 데이터가 존재하지 않습니다.");
            }

            return response.getRows();
        } catch (JAXBException e) {
            throw new OpenApiException("XML 파싱 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void performBatchInsertWithConsistencyCheck(List<OpenApi> batchList) {
        // 데이터베이스에 배치 단위로 insert
        BatchInsertUtil.batchInsert(entityManager, batchList);

        // 데이터 정합성 체크
        boolean isConsistent = dataConsistencyUtil.checkOpenApiDataConsistency(batchList);
        if (!isConsistent) {
            throw new DataIntegrityException("데이터 정합성 체크 실패! 저장된 데이터와 불러온 데이터가 일치하지 않습니다.");
        }
    }

    private void saveLastFetchedStatus(int lastFetchedRow) {
        OpenApiFetchStatus newFetchStatus = new OpenApiFetchStatus();
        newFetchStatus.setLastFetchedRow(lastFetchedRow);
        openApiFetchStatusRepository.save(newFetchStatus);
    }
}
package com.example.cacheproject.openapi.service;

import com.example.cacheproject.common.util.BatchInsertUtil;
import com.example.cacheproject.common.util.DataConsistencyUtil;
import com.example.cacheproject.exception.DataIntegrityException;
import com.example.cacheproject.openapi.dto.OpenApiResponse;
import com.example.cacheproject.openapi.entity.OpenApi;
import com.example.cacheproject.openapi.fetchstatus.entity.OpenApiFetchStatus;
import com.example.cacheproject.openapi.fetchstatus.repository.OpenApiFetchStatusRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
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
public class OpenApiService {

    private static final int MAX_LIMIT = 10_000; // 한 번 API 호출에 최대 삽입 가능한 데이터 개수
    private static final int MAX_REQUEST_SIZE = 1000; // OpenAPI에서 한 번에 요청할 수 있는 최대 데이터 건수
    private final RestTemplate restTemplate;
    private final OpenApiFetchStatusRepository openApiFetchStatusRepository;
    private final DataConsistencyUtil dataConsistencyUtil;
    private final String openApiUrl;

    @PersistenceContext
    private EntityManager entityManager;

    public OpenApiService(RestTemplate restTemplate,
                          OpenApiFetchStatusRepository openApiFetchStatusRepository,
                          DataConsistencyUtil dataConsistencyUtil,
                          @Value("${openapi.url}") String openApiUrl) {
        this.restTemplate = restTemplate;
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

            List<OpenApi> batchList = parseXmlWithJAXB(responseBody);

            // 더 이상 불러올 데이터가 없으면 종료
            if (batchList.isEmpty()) {
                return "더 이상 데이터가 없습니다.";
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

    // JAXB를 사용하여 XML을 파싱하는 메서드
    public List<OpenApi> parseXmlWithJAXB(String xmlResponse) {
        try {
            // JAXBContext를 사용하여 OpenApiResponse 클래스를 unmarshalling합니다.
            JAXBContext context = JAXBContext.newInstance(OpenApiResponse.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(xmlResponse);
            OpenApiResponse response = (OpenApiResponse) unmarshaller.unmarshal(reader);
            return response.getRows(); // OpenApiResponse에서 rows 리스트를 반환합니다.
        } catch (JAXBException e) {
            log.error("XML 파싱 중 오류 발생", e);
            return new ArrayList<>();
        }
    }
}

package com.example.cacheproject.domain.openapi.dto;

import com.example.cacheproject.domain.openapi.entity.OpenApi;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlRootElement(name = "ServiceInternetShopInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class OpenApiResponse {

    @XmlElement(name = "row")
    private List<OpenApi> rows;

    public List<OpenApi> getRows() {
        return rows;
    }

    public void setRows(List<OpenApi> rows) {
        this.rows = rows;
    }
}

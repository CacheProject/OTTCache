package com.example.cacheproject.openapi.dto;

import com.example.cacheproject.openapi.entity.OpenApi;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "root")
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

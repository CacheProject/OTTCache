package com.example.cacheproject.common.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DefaultResponse<T> implements Response<T>, Serializable {
    private static final long serialVersionUID = 1L;

    private T data;

    @JsonCreator
    public DefaultResponse(@JsonProperty("data") T data) {
        this.data = data;
    }

    // 기본 생성자 (필요한 경우)
    public DefaultResponse() {}

    @Override
    public T getData() {
        return this.data;
    }
}
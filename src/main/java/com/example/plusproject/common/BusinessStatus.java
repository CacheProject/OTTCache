package com.example.plusproject.common;

import lombok.Getter;

@Getter
public enum BusinessStatus {

    SITE_SUSPENDED("사이트운영중단"),
    ON_HOLIDAY("휴업중"),
    PROMOTIONAL("광고용(홍보용)"),
    REGISTRATION_MISMATCH("등록정보불일치"),
    UNCONFIRMED("확인안됨"),
    IN_OPERATION("영업중"),
    SITE_CLOSED("사이트폐쇄"),
    UNKNOWN("UNKNOWN");

    private final String description;

    BusinessStatus(String description) {
        this.description = description;
    }

    public static BusinessStatus fromString(String status) {
        for (BusinessStatus businessStatus : BusinessStatus.values()) {
            if (businessStatus.getDescription().equals(status)) {
                return businessStatus;
            }
        }
        return BusinessStatus.UNKNOWN;
    }
}

package com.shotFormLetter.sFL.domain.report.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public enum CateGory {
    PORNOGRAPHY("음란물"),
    PROFANITY("욕설"),
    INAPPROPRIATE_POST("부적절한 게시글"),
    ETC("기타");

    private final String value;

    CateGory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static List<String> getCategoryValues() {
        return Arrays.stream(values())
                .map(CateGory::getValue)
                .collect(Collectors.toList());
    }

    public static CateGory fromValue(String value) {
        for (CateGory category : CateGory.values()) {
            if (category.value.equals(value)) {
                return category;
            }
        }
        // 매칭되는 값이 없을 경우 예외 처리 또는 기본값을 반환할 수 있습니다.
        throw new IllegalArgumentException("Invalid CateGory value: " + value);
    }

}

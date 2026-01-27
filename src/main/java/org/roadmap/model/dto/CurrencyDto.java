package org.roadmap.model.dto;

public class CurrencyDto {
    private final String name;
    private final String code;
    private String fullName;
    private final String sign;

    public CurrencyDto(String name, String code, String sign) {
        this.name = name;
        this.code = code;
        this.sign = sign;
    }

    public CurrencyDto(String name, String code, String fullName, String sign) {
        this.name = name;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSign() {
        return sign;
    }
}

package org.roadmap.model.dto;

public record ExchangeRateRequest(String baseCurrencyCode, String targetCurrencyCode, double rate) {
}

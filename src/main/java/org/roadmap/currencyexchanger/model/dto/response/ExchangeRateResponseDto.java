package org.roadmap.currencyexchanger.model.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;

@JsonPropertyOrder({"id", "baseCurrency", "targetCurrency", "rate"})
public record ExchangeRateResponseDto(Long id, CurrencyResponseDto baseCurrency, CurrencyResponseDto targetCurrency, BigDecimal rate) {
}

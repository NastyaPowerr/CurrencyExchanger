package org.roadmap.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.roadmap.model.dto.CurrencyDto;

@JsonPropertyOrder({"id", "baseCurrency", "targetCurrency", "rate"})
public record ExchangeRateResponse(Long id, CurrencyDto baseCurrency, CurrencyDto targetCurrency, Double rate) {
}

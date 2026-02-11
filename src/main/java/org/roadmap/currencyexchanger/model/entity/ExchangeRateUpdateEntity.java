package org.roadmap.currencyexchanger.model.entity;

import java.math.BigDecimal;

public record ExchangeRateUpdateEntity(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
}

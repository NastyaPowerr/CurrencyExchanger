package org.roadmap.currencyexchanger.entity;

import java.math.BigDecimal;

public record ExchangeRateUpdateEntity(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
}

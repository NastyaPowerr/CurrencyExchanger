package org.roadmap.currencyexchanger.entity;

import java.math.BigDecimal;

public record ExchangeRateUpdate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
}

package org.roadmap.model.entity;

import java.math.BigDecimal;

public record ExchangeRateResponseEntity(
        Long id,
        CurrencyEntity baseCurrency,
        CurrencyEntity targetCurrency,
        BigDecimal rate
) {
}

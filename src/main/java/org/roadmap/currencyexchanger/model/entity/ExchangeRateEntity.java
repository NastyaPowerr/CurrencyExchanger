package org.roadmap.currencyexchanger.model.entity;

import java.math.BigDecimal;

public record ExchangeRateEntity(
        Long id,
        CurrencyEntity baseCurrencyEntity,
        CurrencyEntity targetCurrencyEntity,
        BigDecimal rate
) {
}

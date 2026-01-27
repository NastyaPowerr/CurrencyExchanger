package org.roadmap.model;

import org.roadmap.model.dto.CurrencyDto;

public record ExchangeRateResponse(long id, CurrencyDto baseCurrency, CurrencyDto targetCurrency, double rate) {
}

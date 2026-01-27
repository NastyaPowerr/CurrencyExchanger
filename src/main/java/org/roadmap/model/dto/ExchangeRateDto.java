package org.roadmap.model.dto;

public class ExchangeRateDto {
    private final long baseCurrencyId;
    private final long targetCurrencyId;
    private final double rate;

    public ExchangeRateDto(Long baseCurrencyId, Long targetCurrencyId, double rate) {
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public long getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public long getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public double getRate() {
        return rate;
    }
}

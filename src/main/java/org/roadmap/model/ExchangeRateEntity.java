package org.roadmap.model;

public class ExchangeRateEntity {
    private long id;
    private final long baseCurrencyId;
    private final long targetCurrencyId;
    private final double rate;

    public ExchangeRateEntity(long id, long baseCurrencyId, long targetCurrencyId, double rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public ExchangeRateEntity(long baseCurrencyId, long targetCurrencyId, double rate) {
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public long getId() {
        return id;
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

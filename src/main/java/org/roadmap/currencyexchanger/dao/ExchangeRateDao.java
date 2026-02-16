package org.roadmap.currencyexchanger.dao;

import org.roadmap.currencyexchanger.dto.CurrencyCodePair;
import org.roadmap.currencyexchanger.entity.ExchangeRate;

import java.util.Optional;

public interface ExchangeRateDao extends CrudDao<ExchangeRate> {
    Optional<ExchangeRate> findByCodes(CurrencyCodePair codePair);

    void update(ExchangeRate exchangeRate);
}

package org.roadmap.currencyexchanger.dao;

import org.roadmap.currencyexchanger.dto.CurrencyCodePair;
import org.roadmap.currencyexchanger.entity.ExchangeRate;
import org.roadmap.currencyexchanger.entity.ExchangeRateUpdate;

import java.util.Optional;

public interface ExchangeRateDao extends CrudDao<ExchangeRate> {
    Optional<ExchangeRate> findByCodes(CurrencyCodePair codePair);

    void update(ExchangeRateUpdate exchangeRate);

    ExchangeRate saveFromCodes(ExchangeRateUpdate exchangeRate);
}

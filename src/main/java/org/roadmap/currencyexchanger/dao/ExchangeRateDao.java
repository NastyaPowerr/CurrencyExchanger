package org.roadmap.currencyexchanger.dao;

import org.roadmap.currencyexchanger.entity.CurrencyCodePair;
import org.roadmap.currencyexchanger.entity.ExchangeRateEntity;
import org.roadmap.currencyexchanger.entity.ExchangeRateUpdateEntity;

import java.util.Optional;

public interface ExchangeRateDao extends CrudDao<ExchangeRateEntity> {
    Optional<ExchangeRateEntity> findByCodes(CurrencyCodePair codePair);

    void update(ExchangeRateUpdateEntity exchangeRate);

    ExchangeRateEntity saveFromCodes(ExchangeRateUpdateEntity exchangeRate);
}

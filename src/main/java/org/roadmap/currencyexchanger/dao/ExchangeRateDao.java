package org.roadmap.currencyexchanger.dao;

import org.roadmap.currencyexchanger.model.entity.CurrencyCodePair;
import org.roadmap.currencyexchanger.model.entity.ExchangeRateEntity;
import org.roadmap.currencyexchanger.model.entity.ExchangeRateUpdateEntity;

import java.util.Optional;

public interface ExchangeRateDao extends CrudDao<ExchangeRateEntity> {
    Optional<ExchangeRateEntity> findByCodes(CurrencyCodePair codePair);

    void update(ExchangeRateUpdateEntity exchangeRate);

    ExchangeRateEntity saveFromCodes(ExchangeRateUpdateEntity exchangeRate);
}

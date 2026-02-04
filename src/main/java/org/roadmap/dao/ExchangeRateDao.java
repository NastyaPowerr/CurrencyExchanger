package org.roadmap.dao;

import org.roadmap.model.entity.CurrencyCodePair;
import org.roadmap.model.entity.ExchangeRateEntity;
import org.roadmap.model.entity.ExchangeRateUpdateEntity;

import java.util.Optional;

public interface ExchangeRateDao extends CrudDao<ExchangeRateEntity> {
    Optional<ExchangeRateEntity> findByCodes(CurrencyCodePair codePair);

    void update(ExchangeRateUpdateEntity exchangeRate);
}

package org.roadmap.dao;

import org.roadmap.model.entity.CurrencyCodePair;
import org.roadmap.model.entity.ExchangeRateEntity;
import org.roadmap.model.entity.ExchangeRateUpdateEntity;

public interface ExchangeRateDao extends CrudDao<ExchangeRateEntity> {
    ExchangeRateEntity findByCodes(CurrencyCodePair codePair);

    void update(ExchangeRateUpdateEntity exchangeRate);
}

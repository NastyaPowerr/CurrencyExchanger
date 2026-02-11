package org.roadmap.currencyexchanger.dao;

import org.roadmap.currencyexchanger.model.entity.CurrencyEntity;

public interface CurrencyDao extends CrudDao<CurrencyEntity> {
    CurrencyEntity findByCode(String code);
}

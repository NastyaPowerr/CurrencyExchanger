package org.roadmap.currencyexchanger.dao;

import org.roadmap.currencyexchanger.entity.CurrencyEntity;

public interface CurrencyDao extends CrudDao<CurrencyEntity> {
    CurrencyEntity findByCode(String code);
}

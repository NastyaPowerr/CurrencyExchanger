package org.roadmap.dao;

import org.roadmap.model.entity.CurrencyEntity;

public interface CurrencyDao extends CrudDao<CurrencyEntity> {
    CurrencyEntity findByCode(String code);
}

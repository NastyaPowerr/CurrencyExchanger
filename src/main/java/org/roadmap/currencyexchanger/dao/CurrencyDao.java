package org.roadmap.currencyexchanger.dao;

import org.roadmap.currencyexchanger.entity.Currency;

public interface CurrencyDao extends CrudDao<Currency> {
    Currency findByCode(String code);
}

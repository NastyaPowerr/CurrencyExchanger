package org.roadmap.service;

import org.roadmap.dao.CurrencyDao;
import org.roadmap.model.CurrencyEntity;
import org.roadmap.model.dto.CurrencyDto;

public class CurrencyService {
    private final CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    public void save(CurrencyDto dto) {
        CurrencyEntity entity = new CurrencyEntity(
                dto.getName(),
                dto.getCode(),
                dto.getSign()
        );
        currencyDao.save(entity);
    }
}

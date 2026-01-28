package org.roadmap.service;

import org.roadmap.dao.CurrencyDao;
import org.roadmap.dao.ExchangeRateDao;
import org.roadmap.model.CodePair;
import org.roadmap.model.CurrencyEntity;
import org.roadmap.model.ExchangeRateEntity;
import org.roadmap.model.ExchangeRateResponse;
import org.roadmap.model.dto.CurrencyDto;
import org.roadmap.model.dto.ExchangeRateDto;
import org.roadmap.model.dto.ExchangeRateRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDao;
    private final CurrencyDao currencyDao;

    public ExchangeRateService(ExchangeRateDao exchangeRateDao, CurrencyDao currencyDao) {
        this.exchangeRateDao = exchangeRateDao;
        this.currencyDao = currencyDao;
    }

    public void save(ExchangeRateRequest exchangeRate) {
        long baseCurrencyId = currencyDao.getIdByCode(exchangeRate.baseCurrencyCode());
        long targetCurrencyId = currencyDao.getIdByCode(exchangeRate.targetCurrencyCode());

        if (baseCurrencyId == -1 || targetCurrencyId == -1) {
            throw new RuntimeException("that code isn't saved");
        }

        ExchangeRateEntity entity = new ExchangeRateEntity(
                baseCurrencyId,
                targetCurrencyId,
                exchangeRate.rate()
        );
        exchangeRateDao.save(entity);
    }

    public ExchangeRateResponse getByCode(String code) {
        String baseCurrencyCode = code.substring(0, 3);
        String targetCurrencyCode = code.substring(3);

        long baseCurrencyId = currencyDao.getIdByCode(baseCurrencyCode);
        long targetCurrencyId = currencyDao.getIdByCode(targetCurrencyCode);

        CodePair codePair = new CodePair(baseCurrencyId, targetCurrencyId);
        ExchangeRateEntity rateEntity = exchangeRateDao.getByCode(codePair);

        CurrencyEntity baseCurrency = currencyDao.getByCode(baseCurrencyCode);
        CurrencyEntity targetCurrency = currencyDao.getByCode(targetCurrencyCode);

        CurrencyDto baseCurrencyDto = new CurrencyDto(baseCurrency.getName(), baseCurrency.getCode(), baseCurrency.getSign());
        CurrencyDto targetCurrencyDto = new CurrencyDto(targetCurrency.getName(), targetCurrency.getCode(), targetCurrency.getSign());

        ExchangeRateResponse response = new ExchangeRateResponse(
                rateEntity.getId(),
                baseCurrencyDto,
                targetCurrencyDto,
                rateEntity.getRate()
        );
        return response;
    }

    public List<ExchangeRateDto> getAll() {
        List<ExchangeRateEntity> exchangeRates = exchangeRateDao.findAll();
        return exchangeRates.stream()
                .map(e -> new ExchangeRateDto(e.getBaseCurrencyId(), e.getTargetCurrencyId(), e.getRate()))
                .collect(Collectors.toList());
    }
}

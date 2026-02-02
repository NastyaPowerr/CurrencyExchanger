package org.roadmap.service;

import org.roadmap.dao.CurrencyDao;
import org.roadmap.dao.ExchangeRateDao;
import org.roadmap.model.CurrencyCodePair;
import org.roadmap.model.ExchangeRateResponse;
import org.roadmap.model.ExchangeResponse;
import org.roadmap.model.dto.CurrencyDto;
import org.roadmap.model.dto.ExchangeDto;
import org.roadmap.model.dto.ExchangeRateDto;
import org.roadmap.model.entity.CurrencyEntity;
import org.roadmap.model.entity.ExchangeRateEntity;
import org.roadmap.model.entity.ExchangeRateResponseEntity;
import org.roadmap.model.entity.ExchangeRateSaveEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDao;

    public ExchangeRateService(ExchangeRateDao exchangeRateDao) {
        this.exchangeRateDao = exchangeRateDao;
    }

    public ExchangeRateResponse save(ExchangeRateDto exchangeRate) {
        ExchangeRateSaveEntity entity = new ExchangeRateSaveEntity(
                exchangeRate.baseCurrencyCode(),
                exchangeRate.targetCurrencyCode(),
                exchangeRate.rate()
        );
        exchangeRateDao.save(entity);

        CurrencyCodePair codePair = new CurrencyCodePair(exchangeRate.baseCurrencyCode(), exchangeRate.targetCurrencyCode());
        return getByCode(codePair);
    }

    public ExchangeRateResponse getByCode(CurrencyCodePair codePair) {
        ExchangeRateResponseEntity rateEntity = exchangeRateDao.findByCodes(codePair);

        CurrencyEntity baseCurrency = rateEntity.baseCurrency();
        CurrencyEntity targetCurrency = rateEntity.targetCurrency();

        CurrencyDto baseCurrencyDto = new CurrencyDto(baseCurrency.getId(), baseCurrency.getName(), baseCurrency.getCode(), baseCurrency.getSign());
        CurrencyDto targetCurrencyDto = new CurrencyDto(targetCurrency.getId(), targetCurrency.getName(), targetCurrency.getCode(), targetCurrency.getSign());

        return new ExchangeRateResponse(
                rateEntity.id(),
                baseCurrencyDto,
                targetCurrencyDto,
                rateEntity.rate()
        );
    }

    public List<ExchangeRateResponse> getAll() {
        List<ExchangeRateResponseEntity> exchangeRates = exchangeRateDao.findAll();

        List<ExchangeRateResponse> exchangeRateResponses = new ArrayList<>();

        for (ExchangeRateResponseEntity rateEntity : exchangeRates) {
            CurrencyEntity baseCurrency = rateEntity.baseCurrency();
            CurrencyEntity targetCurrency = rateEntity.targetCurrency();

            CurrencyDto baseDto = new CurrencyDto(
                    baseCurrency.getId(),
                    baseCurrency.getName(),
                    baseCurrency.getCode(),
                    baseCurrency.getSign()
            );
            CurrencyDto targetDto = new CurrencyDto(
                    targetCurrency.getId(),
                    targetCurrency.getName(),
                    targetCurrency.getCode(),
                    targetCurrency.getSign()
            );
            exchangeRateResponses.add(
                    new ExchangeRateResponse(
                            rateEntity.id(),
                            baseDto,
                            targetDto,
                            rateEntity.rate()
                    ));
        }
        return exchangeRateResponses;
    }

    public ExchangeRateResponse update(ExchangeRateDto exchangeRate) {
        CurrencyCodePair codePair = new CurrencyCodePair(exchangeRate.baseCurrencyCode(), exchangeRate.targetCurrencyCode());
        ExchangeRateResponse oldResponse = getByCode(codePair);

        ExchangeRateResponse responseToChange = new ExchangeRateResponse(
                oldResponse.id(),
                oldResponse.baseCurrency(),
                oldResponse.targetCurrency(),
                exchangeRate.rate()
        );

        ExchangeRateEntity updatedEntity = exchangeRateDao.update(responseToChange);
        return new ExchangeRateResponse(
                oldResponse.id(),
                oldResponse.baseCurrency(),
                oldResponse.targetCurrency(),
                updatedEntity.getRate()
        );
    }

    public ExchangeResponse exchange(ExchangeDto exchangeDto) {
        String baseCurrencyCode = exchangeDto.baseCurrencyCode();
        String targetCurrencyCode = exchangeDto.targetCurrencyCode();

        CurrencyCodePair codePair = new CurrencyCodePair(baseCurrencyCode, targetCurrencyCode);
        ExchangeRateResponse entity = getByCode(codePair);
        BigDecimal amount = exchangeDto.amount();

        BigDecimal convertedAmount = entity.rate().multiply(amount);

        return new ExchangeResponse(
                entity.baseCurrency(),
                entity.targetCurrency(),
                entity.rate(),
                amount,
                convertedAmount
        );
    }
}

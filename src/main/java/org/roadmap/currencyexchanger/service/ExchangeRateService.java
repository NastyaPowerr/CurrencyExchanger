package org.roadmap.currencyexchanger.service;

import org.roadmap.currencyexchanger.dao.ExchangeRateDao;
import org.roadmap.currencyexchanger.dto.CurrencyCodePair;
import org.roadmap.currencyexchanger.dto.request.ExchangeRateRequestDto;
import org.roadmap.currencyexchanger.dto.response.ExchangeRateResponseDto;
import org.roadmap.currencyexchanger.entity.Currency;
import org.roadmap.currencyexchanger.entity.ExchangeRate;
import org.roadmap.currencyexchanger.mapper.ExchangeRateMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDao;

    public ExchangeRateService(ExchangeRateDao exchangeRateDao) {
        this.exchangeRateDao = exchangeRateDao;
    }

    public ExchangeRateResponseDto save(ExchangeRateRequestDto exchangeRate) {
        ExchangeRate entity = new ExchangeRate(
                null,
                new Currency(null, null, exchangeRate.baseCurrencyCode(), null),
                new Currency(null, null, exchangeRate.targetCurrencyCode(), null),
                exchangeRate.rate()
        );
        ExchangeRate savedEntity = exchangeRateDao.save(entity);
        return ExchangeRateMapper.INSTANCE.toResponseDto(savedEntity);
    }

    public ExchangeRateResponseDto getByCode(CurrencyCodePair codePair) {
        Optional<ExchangeRate> exchangeRate = exchangeRateDao.findByCodes(codePair);
        if (exchangeRate.isPresent()) {
            return ExchangeRateMapper.INSTANCE.toResponseDto(exchangeRate.get());
        }
        throw new NoSuchElementException("Exchange rate with code pair %s, %s not found.".formatted(
                codePair.baseCurrencyCode(), codePair.targetCurrencyCode()
        ));
    }

    public List<ExchangeRateResponseDto> getAll() {
        List<ExchangeRate> exchangeRates = exchangeRateDao.findAll();
        List<ExchangeRateResponseDto> exchangeRateResponses = new ArrayList<>();

        for (ExchangeRate rateEntity : exchangeRates) {
            exchangeRateResponses.add(ExchangeRateMapper.INSTANCE.toResponseDto(rateEntity));
        }
        return exchangeRateResponses;
    }

    public ExchangeRateResponseDto update(ExchangeRateRequestDto exchangeRate) {
        CurrencyCodePair codePair = new CurrencyCodePair(exchangeRate.baseCurrencyCode(), exchangeRate.targetCurrencyCode());
        ExchangeRate entity = new ExchangeRate(
                null,
                new Currency(null, null, exchangeRate.baseCurrencyCode(), null),
                new Currency(null, null, exchangeRate.targetCurrencyCode(), null),
                exchangeRate.rate()
        );
        exchangeRateDao.update(entity);
        return getByCode(codePair);
    }
}
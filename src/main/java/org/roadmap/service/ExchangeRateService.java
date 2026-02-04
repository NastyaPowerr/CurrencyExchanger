package org.roadmap.service;

import org.roadmap.dao.JdbcExchangeRateDao;
import org.roadmap.mapper.ExchangeRateMapper;
import org.roadmap.model.dto.request.ExchangeRateRequestDto;
import org.roadmap.model.dto.request.ExchangeRequestDto;
import org.roadmap.model.dto.response.ExchangeRateResponseDto;
import org.roadmap.model.dto.response.ExchangeResponseDto;
import org.roadmap.model.entity.CurrencyCodePair;
import org.roadmap.model.entity.ExchangeRateEntity;
import org.roadmap.model.entity.ExchangeRateUpdateEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateService {
    private final JdbcExchangeRateDao exchangeRateDao;

    public ExchangeRateService(JdbcExchangeRateDao exchangeRateDao) {
        this.exchangeRateDao = exchangeRateDao;
    }

    public ExchangeRateResponseDto save(ExchangeRateRequestDto exchangeRate) {
        ExchangeRateUpdateEntity entity = new ExchangeRateUpdateEntity(
                exchangeRate.baseCurrencyCode(),
                exchangeRate.targetCurrencyCode(),
                exchangeRate.rate()
        );
        ExchangeRateEntity savedEntity = exchangeRateDao.saveFromCodes(entity);
        return ExchangeRateMapper.INSTANCE.toResponseDto(savedEntity);
    }

    public ExchangeRateResponseDto getByCode(CurrencyCodePair codePair) {
        ExchangeRateEntity rateEntity = exchangeRateDao.findByCodes(codePair);
        return ExchangeRateMapper.INSTANCE.toResponseDto(rateEntity);
    }

    public List<ExchangeRateResponseDto> getAll() {
        List<ExchangeRateEntity> exchangeRates = exchangeRateDao.findAll();
        List<ExchangeRateResponseDto> exchangeRateResponses = new ArrayList<>();

        for (ExchangeRateEntity rateEntity : exchangeRates) {
            exchangeRateResponses.add(ExchangeRateMapper.INSTANCE.toResponseDto(rateEntity));
        }
        return exchangeRateResponses;
    }

    public ExchangeRateResponseDto update(ExchangeRateRequestDto exchangeRate) {
        CurrencyCodePair codePair = new CurrencyCodePair(exchangeRate.baseCurrencyCode(), exchangeRate.targetCurrencyCode());
        ExchangeRateUpdateEntity entity = new ExchangeRateUpdateEntity(
                exchangeRate.baseCurrencyCode(),
                exchangeRate.targetCurrencyCode(),
                exchangeRate.rate()
        );
        exchangeRateDao.update(entity);
        return getByCode(codePair);
    }

    public ExchangeResponseDto exchange(ExchangeRequestDto exchangeRequestDto) {
        String baseCurrencyCode = exchangeRequestDto.baseCurrencyCode();
        String targetCurrencyCode = exchangeRequestDto.targetCurrencyCode();

        CurrencyCodePair codePair = new CurrencyCodePair(baseCurrencyCode, targetCurrencyCode);
        ExchangeRateResponseDto entity = getByCode(codePair);
        BigDecimal amount = exchangeRequestDto.amount();

        BigDecimal convertedAmount = entity.rate().multiply(amount);

        return new ExchangeResponseDto(
                entity.baseCurrency(),
                entity.targetCurrency(),
                entity.rate(),
                amount,
                convertedAmount
        );
    }
}

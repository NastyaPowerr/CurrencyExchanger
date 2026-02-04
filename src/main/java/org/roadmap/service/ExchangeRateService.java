package org.roadmap.service;

import org.roadmap.dao.JdbcExchangeRateDao;
import org.roadmap.mapper.CurrencyMapper;
import org.roadmap.mapper.ExchangeRateMapper;
import org.roadmap.model.dto.request.ExchangeRateRequestDto;
import org.roadmap.model.dto.request.ExchangeRequestDto;
import org.roadmap.model.dto.response.ExchangeRateResponseDto;
import org.roadmap.model.dto.response.ExchangeResponseDto;
import org.roadmap.model.entity.CurrencyCodePair;
import org.roadmap.model.entity.ExchangeRateEntity;
import org.roadmap.model.entity.ExchangeRateUpdateEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ExchangeRateService {
    private final static RoundingMode BANK_ROUNDING = RoundingMode.HALF_EVEN;
    private final static int SCALE = 2;
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
        Optional<ExchangeRateEntity> rateEntityOpt = exchangeRateDao.findByCodes(codePair);
        if (rateEntityOpt.isPresent()) {
            return ExchangeRateMapper.INSTANCE.toResponseDto(rateEntityOpt.get());
        }
        throw new NoSuchElementException("Exchange rate with code pair %s, %s not found.".formatted(
                codePair.baseCurrencyCode(), codePair.targetCurrencyCode()
        ));
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

        BigDecimal amount = exchangeRequestDto.amount();

        BigDecimal convertedAmount;
        Optional<ExchangeRateEntity> entityOpt = exchangeRateDao.findByCodes(codePair);

        if (entityOpt.isPresent()) {
            ExchangeRateEntity entity = entityOpt.get();
            convertedAmount = entity.rate().multiply(amount);
            return new ExchangeResponseDto(
                    CurrencyMapper.INSTANCE.toResponseDto(entity.baseCurrencyEntity()),
                    CurrencyMapper.INSTANCE.toResponseDto(entity.targetCurrencyEntity()),
                    entity.rate(),
                    amount,
                    convertedAmount
            );
        }

        CurrencyCodePair reverseCodePair = new CurrencyCodePair(targetCurrencyCode, baseCurrencyCode);
        Optional<ExchangeRateEntity> reverseEntityOpt = exchangeRateDao.findByCodes(reverseCodePair);
        if (reverseEntityOpt.isPresent()) {
            ExchangeRateEntity entity = reverseEntityOpt.get();
            BigDecimal rate = BigDecimal.ONE.divide(entity.rate(), SCALE, BANK_ROUNDING);
            convertedAmount = rate.multiply(amount);
            return new ExchangeResponseDto(
                    CurrencyMapper.INSTANCE.toResponseDto(entity.baseCurrencyEntity()),
                    CurrencyMapper.INSTANCE.toResponseDto(entity.targetCurrencyEntity()),
                    entity.rate(),
                    amount,
                    convertedAmount
            );
        }

        CurrencyCodePair firstUsdCodePair = new CurrencyCodePair("USD", baseCurrencyCode);
        CurrencyCodePair secondUsdCodePair = new CurrencyCodePair("USD", targetCurrencyCode);
        Optional<ExchangeRateEntity> firstUsdExchangeRate = exchangeRateDao.findByCodes(firstUsdCodePair);
        Optional<ExchangeRateEntity> secondUsdExchangeRate = exchangeRateDao.findByCodes(secondUsdCodePair);
        if (firstUsdExchangeRate.isPresent() && secondUsdExchangeRate.isPresent()) {
            BigDecimal rate = firstUsdExchangeRate.get().rate().divide(secondUsdExchangeRate.get().rate(), SCALE, BANK_ROUNDING);
            convertedAmount = rate.multiply(amount);
            return new ExchangeResponseDto(
                    CurrencyMapper.INSTANCE.toResponseDto(firstUsdExchangeRate.get().targetCurrencyEntity()),
                    CurrencyMapper.INSTANCE.toResponseDto(firstUsdExchangeRate.get().targetCurrencyEntity()),
                    rate,
                    amount,
                    convertedAmount
            );
        }
        throw new NoSuchElementException("Exchange rate with code pair %s, %s not found.".formatted(
                codePair.baseCurrencyCode(), codePair.targetCurrencyCode()
        ));
    }
}

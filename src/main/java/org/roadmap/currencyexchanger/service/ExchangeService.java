package org.roadmap.currencyexchanger.service;

import org.roadmap.currencyexchanger.dao.ExchangeRateDao;
import org.roadmap.currencyexchanger.entity.ExchangeRate;
import org.roadmap.currencyexchanger.mapper.CurrencyMapper;
import org.roadmap.currencyexchanger.dto.request.ExchangeRequestDto;
import org.roadmap.currencyexchanger.dto.response.ExchangeResponseDto;
import org.roadmap.currencyexchanger.dto.CurrencyCodePair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ExchangeService {
    private final static RoundingMode BANK_ROUNDING = RoundingMode.HALF_EVEN;
    private final static int RATE_SCALE = 6;
    private final static int MONEY_DISPLAY_SCALE = 2;
    private final ExchangeRateDao exchangeRateDao;

    public ExchangeService(ExchangeRateDao exchangeRateDao) {
        this.exchangeRateDao = exchangeRateDao;
    }

    public ExchangeResponseDto exchange(ExchangeRequestDto exchangeRequestDto) {
        String baseCurrencyCode = exchangeRequestDto.baseCurrencyCode();
        String targetCurrencyCode = exchangeRequestDto.targetCurrencyCode();

        CurrencyCodePair codePair = new CurrencyCodePair(baseCurrencyCode, targetCurrencyCode);
        ExchangeRate exchangeEntity = findDirect(codePair)
                .or(() -> findReverse(codePair))
                .or(() -> findCross(codePair))
                .orElseThrow(() -> new NoSuchElementException("Exchange rate with code pair %s, %s not found.".formatted(
                        codePair.baseCurrencyCode(), codePair.targetCurrencyCode()
                )));

        BigDecimal amount = exchangeRequestDto.amount();
        BigDecimal convertedAmount = exchangeEntity.rate().multiply(amount);

        return new ExchangeResponseDto(
                CurrencyMapper.INSTANCE.toResponseDto(exchangeEntity.baseCurrency()),
                CurrencyMapper.INSTANCE.toResponseDto(exchangeEntity.targetCurrency()),
                exchangeEntity.rate().stripTrailingZeros(),
                amount,
                convertedAmount.setScale(MONEY_DISPLAY_SCALE, BANK_ROUNDING)
        );
    }

    private Optional<ExchangeRate> findDirect(CurrencyCodePair codePair) {
        Optional<ExchangeRate> exchangeRateOpt = exchangeRateDao.findByCodes(codePair);
        if (exchangeRateOpt.isPresent()) {
            ExchangeRate extractedEntity = exchangeRateOpt.get();
            BigDecimal rate = extractedEntity.rate();
            return Optional.of(new ExchangeRate(
                    extractedEntity.id(),
                    extractedEntity.baseCurrency(),
                    extractedEntity.targetCurrency(),
                    rate
            ));
        }
        return Optional.empty();
    }

    private Optional<ExchangeRate> findReverse(CurrencyCodePair codePair) {
        CurrencyCodePair reverseCodePair = new CurrencyCodePair(codePair.targetCurrencyCode(), codePair.baseCurrencyCode());
        Optional<ExchangeRate> exchangeRateOpt = exchangeRateDao.findByCodes(reverseCodePair);
        if (exchangeRateOpt.isPresent()) {
            ExchangeRate extractedEntity = exchangeRateOpt.get();
            BigDecimal rate = BigDecimal.ONE.divide(extractedEntity.rate(), RATE_SCALE, BANK_ROUNDING);
            return Optional.of(new ExchangeRate(
                    extractedEntity.id(),
                    extractedEntity.baseCurrency(),
                    extractedEntity.targetCurrency(),
                    rate)
            );
        }
        return Optional.empty();
    }

    private Optional<ExchangeRate> findCross(CurrencyCodePair codePair) {
        CurrencyCodePair firstUsdCodePair = new CurrencyCodePair("USD", codePair.baseCurrencyCode());
        CurrencyCodePair secondUsdCodePair = new CurrencyCodePair("USD", codePair.targetCurrencyCode());
        Optional<ExchangeRate> firstExtractedPairOpt = exchangeRateDao.findByCodes(firstUsdCodePair);
        Optional<ExchangeRate> secondExtractedPairOpt = exchangeRateDao.findByCodes(secondUsdCodePair);

        if (firstExtractedPairOpt.isPresent() && secondExtractedPairOpt.isPresent()) {
            ExchangeRate firstExtractedPair = firstExtractedPairOpt.get();
            ExchangeRate secondExtractedPair = secondExtractedPairOpt.get();
            BigDecimal firstRate = firstExtractedPair.rate();
            BigDecimal secondRate = secondExtractedPair.rate();

            BigDecimal rate = secondRate.divide(firstRate, RATE_SCALE, BANK_ROUNDING);

            return Optional.of(new ExchangeRate(
                    null,
                    firstExtractedPair.targetCurrency(),
                    secondExtractedPair.targetCurrency(),
                    rate)
            );
        }
        return Optional.empty();
    }
}
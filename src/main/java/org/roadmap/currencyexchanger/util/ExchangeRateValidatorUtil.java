package org.roadmap.currencyexchanger.util;

import org.roadmap.currencyexchanger.dto.CurrencyCodePair;
import org.roadmap.currencyexchanger.exception.ExceptionMessages;
import org.roadmap.currencyexchanger.exception.ValidationException;

import java.math.BigDecimal;

public final class ExchangeRateValidatorUtil {
    private static final BigDecimal MIN_RATE = BigDecimal.valueOf(0.00001).stripTrailingZeros();
    private static final BigDecimal MIN_AMOUNT = BigDecimal.ZERO;
    private static final BigDecimal MAX_EXCHANGE_RATE = BigDecimal.valueOf(2000000);
    private static final BigDecimal MAX_AMOUNT = BigDecimal.valueOf(1000000000);

    private ExchangeRateValidatorUtil() {
    }

    public static void validateRate(String rate) {
        validateStringToBigDecimal(
                rate,
                ExceptionMessages.MISSING_EXCHANGE_RATE,
                MIN_RATE,
                String.format(ExceptionMessages.INVALID_EXCHANGE_RATE_NUMBER, MIN_RATE),
                String.format(ExceptionMessages.INVALID_EXCHANGE_RATE_STRING, MIN_RATE)
        );
        if (new BigDecimal(rate).compareTo(MAX_EXCHANGE_RATE) > 0) {
            throw new ValidationException(ExceptionMessages.EXCHANGE_OUT_OF_RANGE);
        }
    }

    public static void validateAmount(String amount) {
        validateStringToBigDecimal(
                amount,
                ExceptionMessages.MISSING_EXCHANGE_AMOUNT,
                MIN_AMOUNT,
                String.format(ExceptionMessages.INVALID_EXCHANGE_AMOUNT_MIN, MIN_AMOUNT),
                String.format(ExceptionMessages.INVALID_EXCHANGE_AMOUNT_STRING, MIN_AMOUNT)
        );
        if (new BigDecimal(amount).compareTo(MAX_AMOUNT) > 0) {
            throw new ValidationException(
                    String.format(ExceptionMessages.INVALID_EXCHANGE_AMOUNT_MAX, MAX_AMOUNT));
        }
    }

    public static void validateCodePair(CurrencyCodePair codePair) {
        if (codePair == null) {
            throw new ValidationException(ExceptionMessages.MISSING_CODE_PAIR);
        }
        try {
            CurrencyValidatorUtil.validateCode(codePair.baseCurrencyCode());
            CurrencyValidatorUtil.validateCode(codePair.targetCurrencyCode());
        } catch (ValidationException ex) {
            throw new ValidationException(ExceptionMessages.MISSING_CODE_PAIR);
        }
        if (codePair.baseCurrencyCode().equals(codePair.targetCurrencyCode())) {
            throw new ValidationException(ExceptionMessages.SAME_CURRENCY);
        }
    }

    private static void validateStringToBigDecimal(
            String parameter,
            String missingParameterError,
            BigDecimal minParameter,
            String invalidParameterMessage,
            String invalidStringParameter
    ) {
        if (parameter == null) {
            throw new ValidationException(missingParameterError);
        }
        try {
            BigDecimal bigDecimalRate = new BigDecimal(parameter);

            if (bigDecimalRate.compareTo(minParameter) < 0) {
                throw new ValidationException(invalidParameterMessage);
            }
        } catch (NumberFormatException ex) {
            throw new ValidationException(invalidStringParameter);
        }
    }
}

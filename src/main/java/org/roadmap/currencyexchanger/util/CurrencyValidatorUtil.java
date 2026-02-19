package org.roadmap.currencyexchanger.util;

import org.roadmap.currencyexchanger.exception.ExceptionMessages;
import org.roadmap.currencyexchanger.exception.ValidationException;

public final class CurrencyValidatorUtil {
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int SIGN_LENGTH = 3;
    private static final String CODE_PATTERN = "[A-Z]{3}";

    private CurrencyValidatorUtil() {
    }

    public static void validateCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new ValidationException(ExceptionMessages.MISSING_CURRENCY_CODE);
        }
        if (!code.matches(CODE_PATTERN)) {
            throw new ValidationException(ExceptionMessages.INVALID_CURRENCY_CODE_FORMAT);
        }
    }

    public static void validateSign(String sign) {
        if (sign == null || sign.trim().isEmpty()) {
            throw new ValidationException(ExceptionMessages.MISSING_CURRENCY_SIGN);
        }
        if (sign.length() > SIGN_LENGTH) {
            throw new ValidationException(ExceptionMessages.INVALID_CURRENCY_SIGN_LENGTH);
        }
    }

    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException(ExceptionMessages.MISSING_CURRENCY_NAME);
        }
        if (name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
            throw new ValidationException(
                    String.format(
                            ExceptionMessages.INVALID_CURRENCY_NAME_LENGTH,
                            MIN_NAME_LENGTH,
                            MAX_NAME_LENGTH
                    ));
        }
    }
}

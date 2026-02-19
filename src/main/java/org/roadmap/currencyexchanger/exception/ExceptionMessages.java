package org.roadmap.currencyexchanger.exception;

public final class ExceptionMessages {
    private ExceptionMessages() {
    }

    public static final String FAILED_SAVE = "Failed during save operation.";
    public static final String FAILED_FETCH_CURRENCY_BY_CODE = "Failed to fetch currency by code %s.";
    public static final String FAILED_FETCH_CURRENCIES = "Failed to fetch all currencies.";
    public static final String FAILED_FETCH_EXCHANGE_RATE = "Failed to fetch exchange rate with code pair %s, %s.";
    public static final String FAILED_FETCH_EXCHANGE_RATES = "Failed to fetch all exchange rates.";
    public static final String FAILED_FETCH_ID_AFTER_SAVE = "Failed to fetch generated id after save operation.";

    public static final String CURRENCY_ALREADY_EXISTS = "Currency with code %s already exists.";
    public static final String CURRENCY_NOT_FOUND = "Currency with code %s not found.";

    public static final String EXCHANGE_RATE_ALREADY_EXISTS = "Exchange rate with code pair %s, %s already exists.";
    public static final String EXCHANGE_RATE_NOT_FOUND = "Exchange rate with pair code %s, %s not found.";

    public static final String MISSING_CURRENCY_NAME = "Currency name is required.";
    public static final String MISSING_CURRENCY_CODE = "Currency code is required.";
    public static final String MISSING_CURRENCY_SIGN = "Currency sign is required.";

    public static final String INVALID_CURRENCY_CODE_FORMAT = "Currency code must be exactly 3 upper-case english letters.";
    public static final String INVALID_CURRENCY_SIGN_LENGTH = "Currency sign must be less or equal 3 characters.";
    public static final String INVALID_CURRENCY_NAME_LENGTH = "Currency name must be between %d and %d letters.";

    public static final String MISSING_EXCHANGE_AMOUNT = "Amount is required.";
    public static final String MISSING_EXCHANGE_RATE = "Exchange rate is required.";
    public static final String MISSING_CODE_PAIR = "Code pair is required. Expected 6 characters (two 3-letter english uppercase codes).";

    public static final String INVALID_EXCHANGE_AMOUNT_MIN = "Amount cannot be lesser than %s.";
    public static final String INVALID_EXCHANGE_AMOUNT_MAX = "Amount should be lesser than %s.";
    public static final String INVALID_EXCHANGE_RATE_NUMBER = "Exchange rate cannot be lesser than %s.";
    public static final String INVALID_EXCHANGE_RATE_STRING = "Exchange rate must be a number and cannot be lesser than %s.";
    public static final String INVALID_EXCHANGE_AMOUNT_STRING = "Exchange amount must be a number and cannot be lesser than %s.";

    public static final String SAME_CURRENCY = "Cannot exchange currency to itself.";
    public static final String EXCHANGE_OUT_OF_RANGE = "Exchange rate should be lesser than %s.";
}

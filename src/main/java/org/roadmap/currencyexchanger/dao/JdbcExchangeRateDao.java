package org.roadmap.currencyexchanger.dao;

import org.roadmap.currencyexchanger.dto.CurrencyCodePair;
import org.roadmap.currencyexchanger.entity.Currency;
import org.roadmap.currencyexchanger.entity.ExchangeRate;
import org.roadmap.currencyexchanger.exception.DatabaseException;
import org.roadmap.currencyexchanger.exception.EntityAlreadyExistsException;
import org.roadmap.currencyexchanger.exception.ExceptionMessages;
import org.roadmap.currencyexchanger.util.ConnectionManagerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class JdbcExchangeRateDao implements ExchangeRateDao {
    private static final String SAVE_WITH_CODES_QUERY = """
            INSERT INTO exchangeRates (base_currency_id, target_currency_id, rate)
            VALUES ((SELECT id FROM currencies WHERE code = ?),
                    (SELECT id FROM currencies WHERE code = ?),
                    ?)
            """;

    private static final String FIND_ALL_QUERY = """
            SELECT
                exchange.id AS exchange_id, exchange.rate AS exchange_rate,
                base.id AS base_id, base.code AS base_code, base.full_name AS base_name, base.sign AS base_sign,
                target.id AS target_id, target.code AS target_code, target.full_name AS target_name, target.sign AS target_sign
            FROM exchangeRates exchange
            JOIN currencies base ON base_currency_id = base.id
            JOIN currencies target ON target_currency_id = target.id
            """;
    private static final String UPDATE_BY_CODES_QUERY = """
            UPDATE exchangeRates
            SET rate = ?
            WHERE base_currency_id = (SELECT id FROM currencies WHERE code = ?)
            AND target_currency_id = (SELECT id FROM currencies WHERE code = ?)
            """;
    private static final String CURRENCY_EXISTS_QUERY = """
            SELECT id, code, full_name, sign
            FROM currencies
            WHERE code = ?
            """;
    private static final String FIND_BY_CURRENCY_CODES =
            FIND_ALL_QUERY + "WHERE base.code = ? AND target.code = ?";
    private static final int CONSTRAINT_UNIQUE_ERROR = 19;

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) {
        try (Connection connection = ConnectionManagerUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_WITH_CODES_QUERY)) {
            statement.setString(1, exchangeRate.baseCurrency().code());
            statement.setString(2, exchangeRate.targetCurrency().code());
            statement.setBigDecimal(3, exchangeRate.rate());

            statement.executeUpdate();

            return findByCodes(new CurrencyCodePair(
                    exchangeRate.baseCurrency().code(),
                    exchangeRate.targetCurrency().code()
            )).orElseThrow(() -> new DatabaseException("Saved but not found"));
        } catch (SQLException ex) {
            if (ex.getErrorCode() == CONSTRAINT_UNIQUE_ERROR) {
                String errorMessage = ex.getMessage();
                handleDuplicateExchangeRate(exchangeRate, errorMessage);
                handleCurrencyMissing(exchangeRate, errorMessage);
            }
            throw new DatabaseException(ExceptionMessages.FAILED_SAVE, ex);
        }
    }

    @Override
    public Optional<ExchangeRate> findByCodes(CurrencyCodePair codePair) {
        try (Connection connection = ConnectionManagerUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CURRENCY_CODES)) {
            statement.setString(1, codePair.baseCurrencyCode());
            statement.setString(2, codePair.targetCurrencyCode());

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapToExchangeRate(result));
                }
            }
        } catch (SQLException ex) {
            checkCurrencyExists(codePair.baseCurrencyCode());
            checkCurrencyExists(codePair.targetCurrencyCode());
            throw new DatabaseException(
                    String.format(
                            ExceptionMessages.FAILED_FETCH_EXCHANGE_RATE,
                            codePair.baseCurrencyCode(),
                            codePair.targetCurrencyCode()
                    ),
                    ex
            );
        }
        return Optional.empty();
    }

    @Override
    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (Connection connection = ConnectionManagerUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                exchangeRates.add(mapToExchangeRate(result));
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ExceptionMessages.FAILED_FETCH_EXCHANGE_RATES, ex);
        }
        return exchangeRates;
    }

    @Override
    public void update(ExchangeRate exchangeRate) {
        try (Connection connection = ConnectionManagerUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BY_CODES_QUERY)) {
            statement.setBigDecimal(1, exchangeRate.rate());
            statement.setString(2, exchangeRate.baseCurrency().code());
            statement.setString(3, exchangeRate.targetCurrency().code());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                checkCurrencyExists(exchangeRate.baseCurrency().code());
                checkCurrencyExists(exchangeRate.targetCurrency().code());
                throw new NoSuchElementException(
                        String.format(
                                ExceptionMessages.EXCHANGE_RATE_NOT_FOUND,
                                exchangeRate.baseCurrency().code(),
                                exchangeRate.targetCurrency().code()
                        ));
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to update exchange rate." + ex);
        }
    }

    private void checkCurrencyExists(String code) {
        try (Connection connection = ConnectionManagerUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(CURRENCY_EXISTS_QUERY)) {
            statement.setString(1, code);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new NoSuchElementException(
                            String.format(ExceptionMessages.CURRENCY_NOT_FOUND, code));
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to check existence of currency with code %s".formatted(code) + ex);
        }
    }

    private ExchangeRate mapToExchangeRate(ResultSet result) throws SQLException {
        Currency baseCurrency = new Currency(
                result.getLong("base_id"),
                result.getString("base_name"),
                result.getString("base_code"),
                result.getString("base_sign")
        );
        Currency targetCurrency = new Currency(
                result.getLong("target_id"),
                result.getString("target_name"),
                result.getString("target_code"),
                result.getString("target_sign")
        );
        return new ExchangeRate(
                result.getLong("exchange_id"),
                baseCurrency,
                targetCurrency,
                result.getBigDecimal("exchange_rate")
        );
    }

    private static void handleCurrencyMissing(ExchangeRate exchangeRate, String errorMessage) {
        if (errorMessage.contains("NOT NULL constraint failed")) {
            if (errorMessage.contains("base_currency_id")) {
                throw new NoSuchElementException(
                        String.format(ExceptionMessages.CURRENCY_NOT_FOUND, exchangeRate.baseCurrency().code())
                );
            }
            if (errorMessage.contains("target_currency_id")) {
                throw new NoSuchElementException(
                        String.format(ExceptionMessages.CURRENCY_NOT_FOUND, exchangeRate.targetCurrency().code())
                );
            }
        }
    }

    private static void handleDuplicateExchangeRate(ExchangeRate exchangeRate, String errorMessage) {
        if (errorMessage.contains("UNIQUE constraint failed")) {
            throw new EntityAlreadyExistsException(
                    String.format(ExceptionMessages.EXCHANGE_RATE_ALREADY_EXISTS,
                            exchangeRate.baseCurrency().code(),
                            exchangeRate.targetCurrency().code()
                    ));
        }
    }
}

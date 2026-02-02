package org.roadmap.dao;

import org.roadmap.model.CurrencyCodePair;
import org.roadmap.model.ExchangeRateResponse;
import org.roadmap.model.entity.CurrencyEntity;
import org.roadmap.model.entity.ExchangeRateEntity;
import org.roadmap.model.entity.ExchangeRateResponseEntity;
import org.roadmap.model.entity.ExchangeRateSaveEntity;
import org.roadmap.util.ConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao {
    private static final String SAVE_WITH_CODES_QUERY = """
            INSERT INTO exchangeRates (base_currency_id, target_currency_id, rate)
            VALUES ((SELECT id FROM currencies WHERE code = ?),
                    (SELECT id FROM currencies WHERE code = ?),
                    ?)
            """;
    private static final String FIND_ALL_QUERY = """
            SELECT
                exchange.id AS exchange_id,
                exchange.rate AS exchange_rate,
                base.id AS base_id,
                base.code AS base_code,
                base.full_name AS base_name,
                base.sign AS base_sign,
                target.id AS target_id,
                target.code AS target_code,
                target.full_name AS target_name,
                target.sign AS target_sign
            FROM exchangeRates exchange
            JOIN currencies base ON base_currency_id = base.id
            JOIN currencies target ON target_currency_id = target.id
            """;
    private static final String UPDATE_QUERY = "UPDATE exchangeRates SET rate = ? WHERE id = ?";
    private static final String FIND_BY_ID = """
            SELECT id, base_currency_id, target_currency_id, rate FROM exchangeRates WHERE id = ?
            """;
    private static final String FIND_BY_CURRENCY_CODES = FIND_ALL_QUERY + "WHERE base.code = ? AND target.code = ?";

    public void save(ExchangeRateSaveEntity exchangeRate) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_WITH_CODES_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, exchangeRate.baseCurrencyCode());
            statement.setString(2, exchangeRate.targetCurrencyCode());
            statement.setBigDecimal(3, exchangeRate.rate());

            statement.executeUpdate();
        } catch (
                SQLException ex) {
            throw new RuntimeException("Exception in ExchangeRateDao.save()" + ex.getMessage());
        }
    }

    public ExchangeRateResponseEntity findByCodes(CurrencyCodePair codePair) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CURRENCY_CODES)) {
            statement.setString(1, codePair.baseCurrencyCode());
            statement.setString(2, codePair.targetCurrencyCode());

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    CurrencyEntity baseCurrency = new CurrencyEntity(
                            result.getLong("base_id"),
                            result.getString("base_name"),
                            result.getString("base_code"),
                            result.getString("base_sign")
                    );
                    CurrencyEntity targetCurrency = new CurrencyEntity(
                            result.getLong("target_id"),
                            result.getString("target_name"),
                            result.getString("target_code"),
                            result.getString("target_sign")
                    );
                    return new ExchangeRateResponseEntity(
                            result.getLong("exchange_id"),
                            baseCurrency,
                            targetCurrency,
                            result.getBigDecimal("exchange_rate")
                    );
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Exception in ExchangeRateDao.getByCode()" + ex.getMessage());
        }
        return null;
    }

    public List<ExchangeRateResponseEntity> findAll() {
        List<ExchangeRateResponseEntity> exchangeRates = new ArrayList<>();
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                CurrencyEntity baseCurrency = new CurrencyEntity(
                        result.getLong("base_id"),
                        result.getString("base_name"),
                        result.getString("base_code"),
                        result.getString("base_sign")
                );
                CurrencyEntity targetCurrency = new CurrencyEntity(
                        result.getLong("target_id"),
                        result.getString("target_name"),
                        result.getString("target_code"),
                        result.getString("target_sign")
                );
                ExchangeRateResponseEntity exchangeRate = new ExchangeRateResponseEntity(
                        result.getLong("exchange_id"),
                        baseCurrency,
                        targetCurrency,
                        result.getBigDecimal("exchange_rate")
                );
                exchangeRates.add(exchangeRate);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Exception in ExchangeRateDao.findAll()" + ex.getMessage());
        }
        return exchangeRates;
    }

    public ExchangeRateEntity update(ExchangeRateResponse exchangeRate) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
            statement.setBigDecimal(1, exchangeRate.rate());
            statement.setLong(2, exchangeRate.id());
            statement.executeUpdate();

            return findById(exchangeRate.id());
        } catch (SQLException ex) {
            throw new RuntimeException("Exception in updateRate(): " + ex.getMessage(), ex);
        }
    }

    private ExchangeRateEntity findById(Long id) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
            statement.setLong(1, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new ExchangeRateEntity(
                        result.getLong("id"),
                        result.getLong("base_currency_id"),
                        result.getLong("target_currency_id"),
                        result.getBigDecimal("rate")
                );
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error finding exchange rate: " + ex.getMessage(), ex);
        }
        return null;
    }
}

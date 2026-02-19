package org.roadmap.currencyexchanger.dao;

import org.roadmap.currencyexchanger.entity.Currency;
import org.roadmap.currencyexchanger.exception.DatabaseException;
import org.roadmap.currencyexchanger.exception.EntityAlreadyExistsException;
import org.roadmap.currencyexchanger.exception.ExceptionMessages;
import org.roadmap.currencyexchanger.util.ConnectionManagerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class JdbcCurrencyDao implements CurrencyDao {
    private static final String SAVE_QUERY = """
            INSERT INTO currencies(code, full_name, sign)
            VALUES (?, ?, ?)
            """;
    private static final String GET_BY_CODE_QUERY = """
            SELECT id, code, full_name, sign
            FROM currencies
            WHERE code = ?
            """;
    private static final String FIND_ALL_QUERY = """
            SELECT id, code, full_name, sign
            FROM currencies
            """;
    private static final int CONSTRAINT_UNIQUE_ERROR = 19;

    @Override
    public Currency save(Currency currency) {
        try (Connection connection = ConnectionManagerUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, currency.code());
            statement.setString(2, currency.name());
            statement.setString(3, currency.sign());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    return new Currency(id, currency.name(), currency.code(), currency.sign());
                }
                throw new DatabaseException(ExceptionMessages.FAILED_FETCH_ID_AFTER_SAVE);
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == CONSTRAINT_UNIQUE_ERROR) {
                throw new EntityAlreadyExistsException(
                        String.format(ExceptionMessages.CURRENCY_ALREADY_EXISTS, currency.code())
                );
            }
            throw new DatabaseException(ExceptionMessages.FAILED_SAVE, ex);
        }
    }

    @Override
    public Currency findByCode(String code) {
        try (Connection connection = ConnectionManagerUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BY_CODE_QUERY)) {
            statement.setString(1, code);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapToCurrency(result);
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(
                    String.format(ExceptionMessages.FAILED_FETCH_CURRENCY_BY_CODE, code), ex
            );
        }
        throw new NoSuchElementException(
                String.format(ExceptionMessages.CURRENCY_NOT_FOUND, code)
        );
    }

    @Override
    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        try (Connection connection = ConnectionManagerUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                currencies.add(mapToCurrency(result));
            }
            return currencies;
        } catch (SQLException ex) {
            throw new DatabaseException(ExceptionMessages.FAILED_FETCH_CURRENCIES, ex);
        }
    }

    private Currency mapToCurrency(ResultSet result) throws SQLException {
        Long id = result.getLong("id");
        String code = result.getString("code");
        String name = result.getString("full_name");
        String sign = result.getString("sign");
        return new Currency(id, name, code, sign);
    }
}

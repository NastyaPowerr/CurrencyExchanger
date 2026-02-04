package org.roadmap.dao;

import org.roadmap.exception.DatabaseException;
import org.roadmap.exception.EntityAlreadyExists;
import org.roadmap.model.entity.CurrencyEntity;
import org.roadmap.util.ConnectionManagerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class CurrencyDao {
    private static final String SAVE_QUERY = "INSERT INTO currencies(code, full_name, sign) values (?, ?, ?)";
    private static final String GET_BY_CODE_QUERY = "SELECT * FROM currencies WHERE code = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM currencies";
    private static final int CONSTRAINT_UNIQUE_ERROR = 19;

    public CurrencyEntity save(CurrencyEntity currencyEntity) {
        try (Connection connection = ConnectionManagerUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, currencyEntity.code());
            statement.setString(2, currencyEntity.name());
            statement.setString(3, currencyEntity.sign());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getLong(1);
                return new CurrencyEntity(id, currencyEntity.name(), currencyEntity.code(), currencyEntity.sign());
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == CONSTRAINT_UNIQUE_ERROR) {
                throw new EntityAlreadyExists("Currency with code %s already exists.".formatted(currencyEntity.code()));
            }
        }
        throw new DatabaseException();
    }

    public CurrencyEntity getByCode(String code) {
        try (Connection connection = ConnectionManagerUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BY_CODE_QUERY)) {
            statement.setString(1, code);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                Long id = result.getLong("id");
                String name = result.getString("full_name");
                String sign = result.getString("sign");
                return new CurrencyEntity(id, code, name, sign);
            }
        } catch (SQLException ex) {
            throw new DatabaseException();
        }
        throw new NoSuchElementException("Currency with code %s not found.".formatted(code));
    }

    public List<CurrencyEntity> findAll() {
        List<CurrencyEntity> currencies = new ArrayList<>();
        try (Connection connection = ConnectionManagerUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                Long id = result.getLong("id");
                String code = result.getString("code");
                String name = result.getString("full_name");
                String sign = result.getString("sign");
                CurrencyEntity currencyEntity = new CurrencyEntity(id, name, code, sign);
                currencies.add(currencyEntity);
            }
        } catch (SQLException ex) {
            throw new DatabaseException();
        }
        return currencies;
    }
}

package org.roadmap.dao;

import org.roadmap.ConnectionManager;
import org.roadmap.model.CurrencyEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CurrencyDao {
    private final ConnectionManager connectionManager;
    private static final String SAVE_QUERY = "INSERT INTO currencies(code, full_name, sign) values (?, ?, ?)";

    public CurrencyDao(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void save(CurrencyEntity currency) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_QUERY)) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getName());
            statement.setString(3, currency.getSign());
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Exception in CurrencyDao.save()" + ex.getMessage());
        }
    }
}

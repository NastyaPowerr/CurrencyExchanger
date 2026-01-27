package org.roadmap.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.roadmap.ConnectionManager;
import org.roadmap.dao.CurrencyDao;
import org.roadmap.model.dto.CurrencyDto;
import org.roadmap.service.CurrencyService;

@WebServlet("/api/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService;

    public CurrencyServlet() {
        ConnectionManager connectionManager = new ConnectionManager();
        CurrencyDao currencyDao = new CurrencyDao(connectionManager);
        this.currencyService = new CurrencyService(currencyDao);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String path = req.getPathInfo();
        String code = path.substring(1);
        CurrencyDto currency = currencyService.get(code);
        System.out.println(currency);
    }
}

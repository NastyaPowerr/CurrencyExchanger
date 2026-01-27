package org.roadmap.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.roadmap.ConnectionManager;
import org.roadmap.dao.CurrencyDao;
import org.roadmap.model.dto.CurrencyDto;
import org.roadmap.service.CurrencyService;

import java.util.List;

@WebServlet("/api/currencies/*")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService;

    public CurrenciesServlet() {
        ConnectionManager connectionManager = new ConnectionManager();
        CurrencyDao currencyDao = new CurrencyDao(connectionManager);
        this.currencyService = new CurrencyService(currencyDao);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");
        CurrencyDto currency = new CurrencyDto(name, code, sign);
        currencyService.save(currency);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        List<CurrencyDto> currencies = currencyService.getAll();
        for (CurrencyDto currency : currencies) {
            System.out.println(currency);
        }
    }
}

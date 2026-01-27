package org.roadmap.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.roadmap.ConnectionManager;
import org.roadmap.dao.CurrencyDao;
import org.roadmap.dao.ExchangeRateDao;
import org.roadmap.model.ExchangeRateResponse;
import org.roadmap.model.dto.ExchangeRateDto;
import org.roadmap.model.dto.ExchangeRateRequest;
import org.roadmap.service.ExchangeRateService;

import java.util.List;

@WebServlet("/api/exchangeRates/*")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService;

    public ExchangeRatesServlet() {
        ConnectionManager connectionManager = new ConnectionManager();
        ExchangeRateDao exchangeRateDao = new ExchangeRateDao(connectionManager);
        CurrencyDao currencyDao = new CurrencyDao(connectionManager);
        this.exchangeRateService = new ExchangeRateService(exchangeRateDao, currencyDao);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        double rate = Double.parseDouble((req.getParameter("rate")));
        ExchangeRateRequest exchangeRate = new ExchangeRateRequest(baseCurrencyCode, targetCurrencyCode, rate);
        exchangeRateService.save(exchangeRate);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String path = req.getPathInfo();
        ExchangeRateResponse response;
        List<ExchangeRateDto> exchangeRates;
        if (path == null || path.equals("/")) {
            exchangeRates = exchangeRateService.getAll();
            for (ExchangeRateDto exchangeRateDto : exchangeRates) {
                System.out.println(exchangeRateDto);
            }
        } else {
            String code = path.substring(1);
            response = exchangeRateService.getByCode(code);
            System.out.println(response);
        }
    }
}

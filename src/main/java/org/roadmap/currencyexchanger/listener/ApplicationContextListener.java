package org.roadmap.currencyexchanger.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.roadmap.currencyexchanger.dao.CurrencyDao;
import org.roadmap.currencyexchanger.dao.ExchangeRateDao;
import org.roadmap.currencyexchanger.dao.JdbcCurrencyDao;
import org.roadmap.currencyexchanger.dao.JdbcExchangeRateDao;
import org.roadmap.currencyexchanger.service.CurrencyService;
import org.roadmap.currencyexchanger.service.ExchangeRateService;
import org.roadmap.currencyexchanger.service.ExchangeService;
import org.roadmap.currencyexchanger.util.ServletResponseUtil;
import tools.jackson.databind.ObjectMapper;

@WebListener
public class ApplicationContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ObjectMapper objectMapper = new ObjectMapper();
        CurrencyDao currencyDao = new JdbcCurrencyDao();
        ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();
        CurrencyService currencyService = new CurrencyService(currencyDao);
        ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeRateDao);
        ExchangeService exchangeService = new ExchangeService(exchangeRateDao);

        ServletContext context = sce.getServletContext();
        context.setAttribute("currencyDao", currencyDao);
        context.setAttribute("exchangeRateDao", exchangeRateDao);
        context.setAttribute("currencyService", currencyService);
        context.setAttribute("exchangeRateService", exchangeRateService);
        context.setAttribute("exchangeService", exchangeService);
        context.setAttribute("objectMapper", objectMapper);
    }
}

package org.roadmap.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.roadmap.dao.CurrencyDao;
import org.roadmap.dao.ExchangeRateDao;
import org.roadmap.service.CurrencyService;
import org.roadmap.service.ExchangeRateService;
import org.roadmap.util.ServletResponseUtil;
import tools.jackson.databind.ObjectMapper;

@WebListener
public class ApplicationContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ObjectMapper objectMapper = new ObjectMapper();
        CurrencyDao currencyDao = new CurrencyDao();
        ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
        CurrencyService currencyService = new CurrencyService(currencyDao);
        ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeRateDao);

        ServletContext context = sce.getServletContext();
        context.setAttribute("currencyDao", currencyDao);
        context.setAttribute("exchangeRateDao", exchangeRateDao);
        context.setAttribute("currencyService", currencyService);
        context.setAttribute("exchangeRateService", exchangeRateService);
        context.setAttribute("objectMapper", objectMapper);

        ServletResponseUtil.init(objectMapper);

        System.out.println("AppContextInitialized");
    }
}

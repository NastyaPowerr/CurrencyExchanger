package org.roadmap.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.roadmap.exception.ValidationException;
import org.roadmap.model.dto.response.ExchangeRateResponseDto;
import org.roadmap.model.dto.request.ExchangeRateRequestDto;
import org.roadmap.service.ExchangeRateService;
import org.roadmap.validator.CurrencyValidator;
import org.roadmap.validator.ExchangeRateValidator;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/api/exchangeRates/*")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.exchangeRateService = (ExchangeRateService) context.getAttribute("exchangeRateService");
        this.objectMapper = (ObjectMapper) context.getAttribute("objectMapper");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");

        Double rate = Double.parseDouble((req.getParameter("rate")));
        BigDecimal bigDecimalRate = BigDecimal.valueOf(rate);
        try {
            CurrencyValidator.validateCode(baseCurrencyCode);
            CurrencyValidator.validateCode(targetCurrencyCode);
            //TODO: method for parsing String rate -> BigDecimalRate
            ExchangeRateValidator.validateRate(bigDecimalRate);
        } catch (ValidationException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(ex.getMessage());
            return;
        }
        ExchangeRateRequestDto exchangeRate = new ExchangeRateRequestDto(baseCurrencyCode, targetCurrencyCode, bigDecimalRate);
        ExchangeRateResponseDto exchangeRateResponseDto = exchangeRateService.save(exchangeRate);


        String jsonResponse = objectMapper.writeValueAsString(exchangeRateResponseDto);
        resp.getWriter().write(jsonResponse);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            List<ExchangeRateResponseDto> exchangeRates = exchangeRateService.getAll();
            String jsonResponse = objectMapper.writeValueAsString(exchangeRates);
            resp.getWriter().write(jsonResponse);
        }
    }
}

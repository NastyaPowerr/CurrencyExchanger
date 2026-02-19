package org.roadmap.currencyexchanger.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.roadmap.currencyexchanger.dto.CurrencyCodePair;
import org.roadmap.currencyexchanger.dto.request.ExchangeRateRequestDto;
import org.roadmap.currencyexchanger.dto.response.ExchangeRateResponseDto;
import org.roadmap.currencyexchanger.service.ExchangeRateService;
import org.roadmap.currencyexchanger.util.ExchangeRateValidatorUtil;
import org.roadmap.currencyexchanger.util.ServletResponseUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.exchangeRateService = (ExchangeRateService) context.getAttribute("exchangeRateService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeRateRequestDto exchangeRate = extractAndValidateExchangeRateRequest(req);
        ExchangeRateResponseDto exchangeRateResponseDto = exchangeRateService.save(exchangeRate);

        ServletResponseUtil.sendSuccessResponse(resp, 201, exchangeRateResponseDto);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRateResponseDto> exchangeRates = exchangeRateService.getAll();
        ServletResponseUtil.sendSuccessResponse(resp, 200, exchangeRates);
    }

    private static ExchangeRateRequestDto extractAndValidateExchangeRateRequest(HttpServletRequest req) {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        CurrencyCodePair codePair = new CurrencyCodePair(baseCurrencyCode, targetCurrencyCode);
        ExchangeRateValidatorUtil.validateCodePair(codePair);
        ExchangeRateValidatorUtil.validateRate(rate);

        BigDecimal bigDecimalRate = new BigDecimal(rate);
        return new ExchangeRateRequestDto(codePair.baseCurrencyCode(), codePair.targetCurrencyCode(), bigDecimalRate);
    }
}

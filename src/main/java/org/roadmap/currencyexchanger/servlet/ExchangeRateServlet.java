package org.roadmap.currencyexchanger.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.roadmap.currencyexchanger.dto.CurrencyCodePair;
import org.roadmap.currencyexchanger.dto.request.ExchangeRateRequestDto;
import org.roadmap.currencyexchanger.dto.response.ExchangeRateResponseDto;
import org.roadmap.currencyexchanger.exception.ExceptionMessages;
import org.roadmap.currencyexchanger.exception.ValidationException;
import org.roadmap.currencyexchanger.service.ExchangeRateService;
import org.roadmap.currencyexchanger.util.ExchangeRateValidatorUtil;
import org.roadmap.currencyexchanger.util.ServletResponseUtil;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.exchangeRateService = (ExchangeRateService) context.getAttribute("exchangeRateService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CurrencyCodePair codePair = extractAndValidateCodePair(req);
        ExchangeRateResponseDto response = exchangeRateService.getByCode(codePair);

        ServletResponseUtil.sendSuccessResponse(resp, 200, response);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeRateRequestDto exchangeRate = extractAndValidateExchangeRateRequest(req);
        ExchangeRateResponseDto exchangeRateResponseDto = exchangeRateService.update(exchangeRate);

        ServletResponseUtil.sendSuccessResponse(resp, 200, exchangeRateResponseDto);
    }

    private ExchangeRateRequestDto extractAndValidateExchangeRateRequest(HttpServletRequest req) throws IOException {
        CurrencyCodePair codePair = extractAndValidateCodePair(req);
        String rateString = req.getReader().readLine();
        if (rateString == null) {
            throw new ValidationException(ExceptionMessages.MISSING_EXCHANGE_RATE);
        }
        rateString = rateString.replace("rate=", "");

        ExchangeRateValidatorUtil.validateRate(rateString);
        BigDecimal bigDecimalRate = new BigDecimal(rateString);
        return new ExchangeRateRequestDto(codePair.baseCurrencyCode(), codePair.targetCurrencyCode(), bigDecimalRate);
    }

    private static CurrencyCodePair extractAndValidateCodePair(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            throw new ValidationException(ExceptionMessages.MISSING_CODE_PAIR);
        }
        String inputCodePair = path.substring(1);
        if (inputCodePair.length() != 6) {
            throw new ValidationException(ExceptionMessages.MISSING_CODE_PAIR);
        }
        String baseCurrencyCode = inputCodePair.substring(0, 3);
        String targetCurrencyCode = inputCodePair.substring(3);

        CurrencyCodePair codePair = new CurrencyCodePair(baseCurrencyCode, targetCurrencyCode);
        ExchangeRateValidatorUtil.validateCodePair(codePair);
        return codePair;
    }
}

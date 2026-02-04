package org.roadmap.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.roadmap.exception.DatabaseException;
import org.roadmap.exception.ValidationException;
import org.roadmap.model.dto.request.ExchangeRateRequestDto;
import org.roadmap.model.dto.response.ExchangeRateResponseDto;
import org.roadmap.model.entity.CurrencyCodePair;
import org.roadmap.service.ExchangeRateService;
import org.roadmap.util.ExchangeRateValidatorUtil;
import org.roadmap.util.ServletResponseUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.NoSuchElementException;

@WebServlet("/api/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.exchangeRateService = (ExchangeRateService) context.getAttribute("exchangeRateService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            CurrencyCodePair codePair = extractAndValidateCodePair(req);
            ExchangeRateResponseDto response = exchangeRateService.getByCode(codePair);

            ServletResponseUtil.sendSuccessResponse(resp, response);
        } catch (ValidationException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 400, ex.getMessage());
        } catch (NoSuchElementException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 404, ex.getMessage());
        } catch (DatabaseException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 500, "Internal error.");
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ExchangeRateRequestDto exchangeRate = extractAndValidateExchangeRateRequest(req);
            ExchangeRateResponseDto exchangeRateResponseDto = exchangeRateService.update(exchangeRate);

            ServletResponseUtil.sendSuccessResponse(resp, exchangeRateResponseDto);
        } catch (ValidationException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 400, ex.getMessage());
        } catch (NoSuchElementException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 404, ex.getMessage());
        } catch (DatabaseException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 500, "Internal error.");
        }
    }

    private ExchangeRateRequestDto extractAndValidateExchangeRateRequest(HttpServletRequest req) throws IOException {
        CurrencyCodePair codePair = extractAndValidateCodePair(req);
        String rateString = req.getReader().readLine();
        if (rateString == null) {
            throw new ValidationException(ExchangeRateValidatorUtil.MISSING_RATE_ERROR);
        }
        rateString = rateString.replace("rate=", "");

        ExchangeRateValidatorUtil.validateRate(rateString);
        BigDecimal bigDecimalRate = new BigDecimal(rateString);
        return new ExchangeRateRequestDto(codePair.baseCurrencyCode(), codePair.targetCurrencyCode(), bigDecimalRate);
    }

    private static CurrencyCodePair extractAndValidateCodePair(HttpServletRequest req) {
        String path = req.getPathInfo();
        String code = path.substring(1);

        String baseCurrencyCode = code.substring(0, 3);
        String targetCurrencyCode = code.substring(3);

        CurrencyCodePair codePair = new CurrencyCodePair(baseCurrencyCode.toUpperCase(), targetCurrencyCode.toUpperCase());
        ExchangeRateValidatorUtil.validateCodePair(codePair);
        return codePair;
    }
}

package org.roadmap.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.roadmap.exception.DatabaseException;
import org.roadmap.exception.EntityAlreadyExistsException;
import org.roadmap.exception.ValidationException;
import org.roadmap.model.dto.request.ExchangeRateRequestDto;
import org.roadmap.model.dto.response.ExchangeRateResponseDto;
import org.roadmap.model.entity.CurrencyCodePair;
import org.roadmap.service.ExchangeRateService;
import org.roadmap.util.ExchangeRateValidatorUtil;
import org.roadmap.util.ServletResponseUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@WebServlet("/exchangeRates/*")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.exchangeRateService = (ExchangeRateService) context.getAttribute("exchangeRateService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ExchangeRateRequestDto exchangeRate = extractAndValidateExchangeRateRequest(req);
            ExchangeRateResponseDto exchangeRateResponseDto = exchangeRateService.save(exchangeRate);

            ServletResponseUtil.sendSuccessResponse(resp, exchangeRateResponseDto);
        } catch (ValidationException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 400, ex.getMessage());
        } catch (NoSuchElementException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 404, ex.getMessage());
        } catch (EntityAlreadyExistsException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 409, ex.getMessage());
        } catch (DatabaseException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 500, "Internal error.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<ExchangeRateResponseDto> exchangeRates = exchangeRateService.getAll();
            ServletResponseUtil.sendSuccessResponse(resp, exchangeRates);
        } catch (DatabaseException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 500, "Internal error.");
        }
    }

    private static ExchangeRateRequestDto extractAndValidateExchangeRateRequest(HttpServletRequest req) {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        CurrencyCodePair codePair = new CurrencyCodePair(baseCurrencyCode.toUpperCase(), targetCurrencyCode.toUpperCase());
        ExchangeRateValidatorUtil.validateCodePair(codePair);
        ExchangeRateValidatorUtil.validateRate(rate);

        BigDecimal bigDecimalRate = new BigDecimal(rate);
        return new ExchangeRateRequestDto(codePair.baseCurrencyCode(), codePair.targetCurrencyCode(), bigDecimalRate);
    }
}

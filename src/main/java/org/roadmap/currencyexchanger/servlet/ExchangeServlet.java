package org.roadmap.currencyexchanger.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.roadmap.currencyexchanger.exception.DatabaseException;
import org.roadmap.currencyexchanger.exception.ValidationException;
import org.roadmap.currencyexchanger.dto.request.ExchangeRequestDto;
import org.roadmap.currencyexchanger.dto.response.ExchangeResponseDto;
import org.roadmap.currencyexchanger.entity.CurrencyCodePair;
import org.roadmap.currencyexchanger.service.ExchangeService;
import org.roadmap.currencyexchanger.util.ExchangeRateValidatorUtil;
import org.roadmap.currencyexchanger.util.ServletResponseUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.NoSuchElementException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private ExchangeService exchangeService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.exchangeService = (ExchangeService) context.getAttribute("exchangeService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ExchangeRequestDto exchangeRequestDto = extractAndValidateExchangeRequest(req);
            ExchangeResponseDto response = exchangeService.exchange(exchangeRequestDto);

            ServletResponseUtil.sendSuccessResponse(resp, 200, response);
        } catch (ValidationException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 400, ex.getMessage());
        } catch (NoSuchElementException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 404, ex.getMessage());
        } catch (DatabaseException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 500, "Internal error.");
        }
    }

    private static ExchangeRequestDto extractAndValidateExchangeRequest(HttpServletRequest req) {
        String fromCode = req.getParameter("from");
        String toCode = req.getParameter("to");
        String amount = req.getParameter("amount");

        ExchangeRateValidatorUtil.validateCodePair(new CurrencyCodePair(fromCode, toCode));
        ExchangeRateValidatorUtil.validateAmount(amount);

        BigDecimal bigDecimalAmount = new BigDecimal(amount);
        return new ExchangeRequestDto(fromCode, toCode, bigDecimalAmount);
    }
}

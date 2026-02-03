package org.roadmap.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.roadmap.exception.DatabaseException;
import org.roadmap.exception.ValidationException;
import org.roadmap.model.dto.request.ExchangeRequestDto;
import org.roadmap.model.dto.response.ExchangeResponseDto;
import org.roadmap.service.ExchangeRateService;
import org.roadmap.util.CurrencyValidatorUtil;
import org.roadmap.util.ExchangeRateValidatorUtil;
import org.roadmap.util.ServletResponseUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.NoSuchElementException;

@WebServlet("/api/exchange/*")
public class ExchangeServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.exchangeRateService = (ExchangeRateService) context.getAttribute("exchangeRateService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ExchangeRequestDto exchangeRequestDto = extractAndValidateExchangeRequest(req);
            ExchangeResponseDto response = exchangeRateService.exchange(exchangeRequestDto);

            ServletResponseUtil.sendSuccessResponse(resp, response);
        } catch (ValidationException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 400, ex.getMessage());
        } catch (NoSuchElementException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 404, ex.getMessage());
        } catch (DatabaseException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 500, ex.getMessage());
        }
    }

    private static ExchangeRequestDto extractAndValidateExchangeRequest(HttpServletRequest req) {
        String fromCode = req.getParameter("from");
        String toCode = req.getParameter("to");
        String amount = req.getParameter("amount");

        CurrencyValidatorUtil.validateCode(fromCode);
        CurrencyValidatorUtil.validateCode(toCode);
        ExchangeRateValidatorUtil.validateAmount(amount);

        BigDecimal bigDecimalAmount = new BigDecimal(amount);
        return new ExchangeRequestDto(fromCode, toCode, bigDecimalAmount);
    }
}

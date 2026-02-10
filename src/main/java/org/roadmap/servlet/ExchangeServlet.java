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
import org.roadmap.model.entity.CurrencyCodePair;
import org.roadmap.service.ExchangeService;
import org.roadmap.util.CurrencyValidatorUtil;
import org.roadmap.util.ExchangeRateValidatorUtil;
import org.roadmap.util.ServletResponseUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.NoSuchElementException;

@WebServlet("/exchange/*")
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

            ServletResponseUtil.sendSuccessResponse(resp, response);
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
        CurrencyValidatorUtil.validateCode(fromCode.toUpperCase());
        CurrencyValidatorUtil.validateCode(toCode.toUpperCase());
        ExchangeRateValidatorUtil.validateAmount(amount);

        BigDecimal bigDecimalAmount = new BigDecimal(amount);
        return new ExchangeRequestDto(fromCode.toUpperCase(), toCode.toUpperCase(), bigDecimalAmount);
    }
}

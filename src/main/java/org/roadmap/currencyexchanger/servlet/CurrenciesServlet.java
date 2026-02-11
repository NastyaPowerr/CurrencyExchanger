package org.roadmap.currencyexchanger.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.roadmap.currencyexchanger.exception.DatabaseException;
import org.roadmap.currencyexchanger.exception.EntityAlreadyExistsException;
import org.roadmap.currencyexchanger.exception.ValidationException;
import org.roadmap.currencyexchanger.model.dto.request.CurrencyRequestDto;
import org.roadmap.currencyexchanger.model.dto.response.CurrencyResponseDto;
import org.roadmap.currencyexchanger.service.CurrencyService;
import org.roadmap.currencyexchanger.util.CurrencyValidatorUtil;
import org.roadmap.currencyexchanger.util.ServletResponseUtil;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyService currencyService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.currencyService = (CurrencyService) context.getAttribute("currencyService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            CurrencyRequestDto requestCurrency = extractAndValidateDto(req);
            CurrencyResponseDto responseCurrency = currencyService.save(requestCurrency);

            ServletResponseUtil.sendSuccessResponse(resp, 201, responseCurrency);
        } catch (ValidationException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 400, ex.getMessage());
        } catch (EntityAlreadyExistsException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 409, ex.getMessage());
        } catch (DatabaseException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 500, "Internal error.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<CurrencyResponseDto> currencies = currencyService.getAll();
            ServletResponseUtil.sendSuccessResponse(resp, 200, currencies);
        } catch (DatabaseException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 500, "Internal error.");
        }
    }

    private static CurrencyRequestDto extractAndValidateDto(HttpServletRequest req) {
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        CurrencyValidatorUtil.validateCode(code);
        CurrencyValidatorUtil.validateName(name);
        CurrencyValidatorUtil.validateSign(sign);

        return new CurrencyRequestDto(name, code.toUpperCase(), sign);
    }
}

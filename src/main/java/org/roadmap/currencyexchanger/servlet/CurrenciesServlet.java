package org.roadmap.currencyexchanger.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.roadmap.currencyexchanger.dto.request.CurrencyRequestDto;
import org.roadmap.currencyexchanger.dto.response.CurrencyResponseDto;
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
        CurrencyRequestDto requestCurrency = extractAndValidateDto(req);
        CurrencyResponseDto responseCurrency = currencyService.save(requestCurrency);

        ServletResponseUtil.sendSuccessResponse(resp, 201, responseCurrency);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<CurrencyResponseDto> currencies = currencyService.getAll();
        ServletResponseUtil.sendSuccessResponse(resp, 200, currencies);
    }

    private static CurrencyRequestDto extractAndValidateDto(HttpServletRequest req) {
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        CurrencyValidatorUtil.validateCode(code);
        CurrencyValidatorUtil.validateName(name);
        CurrencyValidatorUtil.validateSign(sign);

        return new CurrencyRequestDto(name, code, sign);
    }
}

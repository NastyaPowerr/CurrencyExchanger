package org.roadmap.currencyexchanger.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.roadmap.currencyexchanger.dto.response.CurrencyResponseDto;
import org.roadmap.currencyexchanger.service.CurrencyService;
import org.roadmap.currencyexchanger.util.CurrencyValidatorUtil;
import org.roadmap.currencyexchanger.util.ServletResponseUtil;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyService currencyService;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.currencyService = (CurrencyService) context.getAttribute("currencyService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = extractAndValidateCode(req);
        CurrencyResponseDto currency = currencyService.getByCode(code);

        ServletResponseUtil.sendSuccessResponse(resp, 200, currency);
    }

    private static String extractAndValidateCode(HttpServletRequest req) {
        String path = req.getPathInfo();
        String code = path.substring(1);
        CurrencyValidatorUtil.validateCode(code);
        return code;
    }
}

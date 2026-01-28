package org.roadmap.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.roadmap.ConnectionManager;
import org.roadmap.dao.CurrencyDao;
import org.roadmap.model.dto.CurrencyDto;
import org.roadmap.service.CurrencyService;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@WebServlet("/api/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService;
    private final ObjectMapper objectMapper;

    public CurrencyServlet() {
        ConnectionManager connectionManager = new ConnectionManager();
        CurrencyDao currencyDao = new CurrencyDao(connectionManager);
        this.currencyService = new CurrencyService(currencyDao);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String path = req.getPathInfo();
        String code = path.substring(1);
        CurrencyDto currency = currencyService.get(code);

        String jsonResponse = objectMapper.writeValueAsString(currency);
        resp.getWriter().write(jsonResponse);
    }
}

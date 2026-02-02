package org.roadmap.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.roadmap.exception.ValidationException;
import org.roadmap.model.dto.response.ExchangeResponseDto;
import org.roadmap.model.dto.request.ExchangeRequestDto;
import org.roadmap.service.ExchangeRateService;
import org.roadmap.validator.CurrencyValidator;
import org.roadmap.validator.ExchangeRateValidator;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/api/exchange/*")
public class ExchangeServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        ServletContext context = getServletContext();
        this.exchangeRateService = (ExchangeRateService) context.getAttribute("exchangeRateService");
        this.objectMapper = (ObjectMapper) context.getAttribute("objectMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String from = req.getParameter("from");
        String to = req.getParameter("to");
        BigDecimal amount = BigDecimal.valueOf(Long.parseLong(req.getParameter("amount")));

        try {
            CurrencyValidator.validateCode(from);
            CurrencyValidator.validateCode(to);
            ExchangeRateValidator.validateAmount(amount);
        } catch (ValidationException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(ex.getMessage());
            return;
        }

        ExchangeRequestDto exchangeRequestDto = new ExchangeRequestDto(from, to, amount);
        ExchangeResponseDto response = exchangeRateService.exchange(exchangeRequestDto);

        String jsonResponse = objectMapper.writeValueAsString(response);
        resp.getWriter().write(jsonResponse);
    }
}

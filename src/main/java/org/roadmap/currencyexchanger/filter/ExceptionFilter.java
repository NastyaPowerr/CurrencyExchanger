package org.roadmap.currencyexchanger.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.roadmap.currencyexchanger.exception.DatabaseException;
import org.roadmap.currencyexchanger.exception.EntityAlreadyExistsException;
import org.roadmap.currencyexchanger.exception.ValidationException;
import org.roadmap.currencyexchanger.util.ServletResponseUtil;

import java.io.IOException;
import java.util.NoSuchElementException;

@WebFilter("/*")
public class ExceptionFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (ValidationException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 400, ex.getMessage());
        } catch (EntityAlreadyExistsException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 409, ex.getMessage());
        } catch (NoSuchElementException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 404, ex.getMessage());
        } catch (DatabaseException ex) {
            ServletResponseUtil.sendErrorResponse(resp, 500, "Internal error.");
        }
    }
}

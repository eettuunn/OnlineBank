package ru.hits.coreservice.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static ru.hits.coreservice.security.SecurityConst.HEADER_API_KEY;

/**
 * Фильтр, который проверяет наличие API-ключа в интеграционных запросах и устанавливает аутентификацию.
 */
@RequiredArgsConstructor
@Slf4j
class ApiIntegrationFilter extends OncePerRequestFilter {

    /**
     * API-ключ.
     */
    private final String apiKey;

    /**
     * Метод, проверяющий наличие API-ключа и устанавливающий аутентификацию.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (Objects.equals(httpServletRequest.getHeader(HEADER_API_KEY), apiKey)) {
            SecurityContextHolder.getContext().setAuthentication(new IntegrationAuthentication());
            log.info("API-ключ {} успешно аутентифицирован.", apiKey);
        }
        else {
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            log.error("Неавторизованный запрос с API-ключом {}.", apiKey);
            return;
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}

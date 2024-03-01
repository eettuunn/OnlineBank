package ru.hits.coreservice.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static ru.hits.coreservice.security.SecurityConst.HEADER_JWT;

/**
 * Фильтр для обработки JWT-токена, полученного из заголовка "Authorization".
 * Проверяет валидность токена и устанавливает аутентификацию пользователя.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    /**
     * Сервис для работы с JWT-токеном.
     */
    private final JWTUtil jwtUtil;

    /**
     * Метод для обработки каждого запроса и проверки валидности JWT-токена.
     * Если токен валидный, то устанавливает аутентификацию пользователя.
     *
     * @param httpServletRequest HTTP-запрос.
     * @param httpServletResponse HTTP-ответ.
     * @param filterChain цепочка фильтров.
     * @throws ServletException в случае ошибки при обработке запроса.
     * @throws IOException в случае ошибки ввода-вывода.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = httpServletRequest.getHeader(HEADER_JWT);

        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            if (jwt.isBlank()) {
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                log.info("Невалидный токен. JWT-токен пустой.");
            } else {
                try {
                    UUID id = UUID.fromString(jwtUtil.validateTokenAndRetrieveClaim(jwt).get(0));
                    var userData = new JwtUserData(id);
                    var authentication = new JwtAuthentication(userData);

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.info("Аутентификация пользователя с ID {} прошла успешно.", id);
                    }
                } catch (JWTVerificationException exception) {
                    httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                    log.info("Невалидный токен. JWT-токен не прошел проверку.");
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}

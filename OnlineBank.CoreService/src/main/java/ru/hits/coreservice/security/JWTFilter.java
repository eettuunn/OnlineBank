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
import java.util.ArrayList;
import java.util.List;
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
                    List<String> claims = jwtUtil.validateTokenAndRetrieveClaim(jwt);
                    UUID id = UUID.fromString(claims.get(0));
                    String name = claims.get(1);
                    String email = claims.get(2);
                    Boolean ban = Boolean.parseBoolean(claims.get(3).toLowerCase());
                    List<String> roles = convertStringToList(claims.get(4));

                    var userData = new JwtUserData(id, name, email, ban, roles);
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

    public List<String> convertStringToList(String listString) {
        // Удаление символов '[' и ']'
        String trimmedString = listString.substring(1, listString.length() - 1);

        // Разделение строки на элементы списка
        String[] elements = trimmedString.split(", ");

        // Создание нового списка
        List<String> resultList = new ArrayList<>();

        // Добавление элементов в список
        for (String element : elements) {
            resultList.add(element);
        }

        return resultList;
    }


}

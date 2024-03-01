package ru.hits.coreservice.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * Класс, представляющий аутентификацию по JWT-токену.
 */
@Slf4j
public class JwtAuthentication extends AbstractAuthenticationToken {

    /**
     * Конструктор, в котором создается новый экземпляр класса {@link JwtAuthentication}
     * и устанавливается аутентификация.
     *
     * @param jwtUserData данные, которые можно достать из аутентификации.
     */
    public JwtAuthentication(JwtUserData jwtUserData) {
        super(null);
        this.setDetails(jwtUserData);
        setAuthenticated(true);
        log.info("Аутентификация пользователя с ID {} прошла успешно.", jwtUserData.getId());
    }

    /**
     * Возвращает данные, необходимые для аутентификации. В данном случае - null.
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * Возвращает данные об аутентифицированном пользователе.
     *
     * @return данные об аутентифицированном пользователе.
     */
    @Override
    public Object getPrincipal() {
        return getDetails();
    }

}

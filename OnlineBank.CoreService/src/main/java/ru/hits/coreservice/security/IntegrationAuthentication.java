package ru.hits.coreservice.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * Класс, представляющий аутентификацию при интеграционных запросах.
 */
@Slf4j
public class IntegrationAuthentication extends AbstractAuthenticationToken {

    /**
     * Конструктор, в котором создается новый экземпляр класса {@link IntegrationAuthentication}
     * и устанавливается аутентификация.
     */
    public IntegrationAuthentication() {
        super(null);
        setAuthenticated(true);
        log.info("Успешная аутентификация для интеграционного запроса.");
    }

    /**
     * Возвращает данные, необходимые для аутентификации. В данном случае - null.
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * Возвращает данные об аутентифицированном пользователе. В данном случае - null.
     */
    @Override
    public Object getPrincipal() {
        return null;
    }

}

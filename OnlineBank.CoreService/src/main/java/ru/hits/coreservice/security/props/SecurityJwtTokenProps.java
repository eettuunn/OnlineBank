package ru.hits.coreservice.security.props;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Класс, содержащий свойства JWT-токена системы безопасности.
 */
@Getter
@Setter
@ToString
public class SecurityJwtTokenProps {

    /**
     * Массив путей, для которых не требуется авторизация для доступа.
     */
    private String[] permitAll;

    /**
     * Секретный ключ для подписи JWT-токена.
     */
    private String secret;

    /**
     * Издатель JWT-токена.
     */
    private String issuer;

    /**
     * Тема JWT-токена.
     */
    private String subject;

    /**
     * Корневой путь API-запросов.
     */
    private String rootPath;

}

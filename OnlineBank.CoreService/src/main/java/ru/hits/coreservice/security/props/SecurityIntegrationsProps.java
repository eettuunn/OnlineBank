package ru.hits.coreservice.security.props;

import lombok.*;

/**
 * Класс, содержащий свойства необходимые для интеграционных запросов.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SecurityIntegrationsProps {

    /**
     * API-ключ для доступа к системе безопасности.
     */
    private String apiKey;

    /**
     * Корневой путь интеграционных запросов.
     */
    private String rootPath;

}

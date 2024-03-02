//    package ru.hits.coreservice.security.props;
//
//    import lombok.*;
//    import org.springframework.boot.context.properties.ConfigurationProperties;
//
//    /**
//     * Класс, содержащий свойства безопасности приложения.
//     */
//    @ConfigurationProperties("app.security")
//    @Getter
//    @AllArgsConstructor
//    @NoArgsConstructor
//    @Setter
//    @ToString
//    public class SecurityProps {
//
//        /**
//         * Свойства JWT-токена системы безопасности.
//         */
//        private SecurityJwtTokenProps jwtToken;
//
//        /**
//         * Свойства интеграционных запросов.
//         */
//        private SecurityIntegrationsProps integrations;
//
//    }

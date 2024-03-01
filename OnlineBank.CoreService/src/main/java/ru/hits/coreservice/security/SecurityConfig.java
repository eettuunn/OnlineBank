package ru.hits.coreservice.security;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.hits.coreservice.security.props.SecurityProps;

import java.util.Objects;

/**
 * Класс, содержащий настройки авторизации и аутентификации для приложения, а также включающий фильтр JWT.
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    /**
     * Фильтр для обработки JWT-токена.
     */
    private final JWTFilter jwtFilter;

    /**
     * Свойства безопасности приложения.
     */
    private final SecurityProps securityProps;

    /**
     * Метод, который настраивает правила безопасности и включает фильтр JWT.
     *
     * @param http объект класса {@link HttpSecurity} для настройки правил безопасности.
     */
    @SneakyThrows
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) {
        log.info("Настройка правил безопасности и включение фильтра JWT.");
        http = http.requestMatcher(request -> Objects.nonNull(request.getServletPath())
                        && request.getServletPath().startsWith(securityProps.getJwtToken().getRootPath()))
                .authorizeRequests()
                .antMatchers(securityProps.getJwtToken().getPermitAll()).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class
                );
        return finalize(http);
    }

    /**
     * Метод, который настраивает правила безопасности для интеграции между сервисами.
     *
     * @param http объект класса {@link HttpSecurity} для настройки правил безопасности.
     * @return объект класса {@link SecurityFilterChain} для конечной настройки.
     */
    @SneakyThrows
    @Bean
    public SecurityFilterChain filterChainIntegration(HttpSecurity http) {
        log.info("Настройка правил безопасности для интеграции между сервисами.");
        http = http.requestMatcher(request -> Objects.nonNull(request.getServletPath())
                        && request.getServletPath().startsWith(securityProps.getIntegrations().getRootPath()))
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(
                        new ApiIntegrationFilter(securityProps.getIntegrations().getApiKey()),
                        UsernamePasswordAuthenticationFilter.class
                );
        return finalize(http);
    }

    /**
     * Метод, который настраивает правила безопасности для запрета всех запросов,
     * кроме тех, что имеют отдельную настройку.
     *
     * @param http объект класса {@link HttpSecurity} для настройки правил безопасности.
     * @return объект класса {@link SecurityFilterChain} для конечной настройки.
     */
    @SneakyThrows
    @Bean
    public SecurityFilterChain filterChainDenyAll(HttpSecurity http) {
        log.info("Настройка правил безопасности для запрета всех запросов, кроме тех, что имеют отдельную настройку.");
        http = http.requestMatcher(request -> Objects.nonNull(request.getServletPath())
                        && !request.getServletPath().startsWith(securityProps.getJwtToken().getRootPath())
                        && !request.getServletPath().startsWith(securityProps.getIntegrations().getRootPath()))
                .authorizeRequests()
                .anyRequest().permitAll()
                .and();
        return finalize(http);
    }

    /**
     * Метод, заверщающий настройку объекта класса {@link HttpSecurity} и
     * возвращающий объект класса {@link SecurityFilterChain}.
     *
     * @param http объект класса {@link HttpSecurity} для завершения настройки.
     * @return объект класса {@link SecurityFilterChain}.
     */
    @SneakyThrows
    private SecurityFilterChain finalize(HttpSecurity http) {
        log.info("Завершение настройки правил безопасности и возвращение объекта класса SecurityFilterChain.");
        return http.csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors()
                .and()
                .build();
    }

}

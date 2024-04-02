package ru.hits.coreservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * Утилитарный класс для генерации и проверки JWT-токенов.
 */
@Component
@Slf4j
public class JWTUtil {

    /**
     * Секретный ключ.
     */
    @Value("${app.security.jwt-token.secret}")
    private String secret;

    /**
     * Приложение, из которого отправляется токен.
     */
    @Value("${app.security.jwt-token.issuer}")
    private String issuer;

    @Value("${app.security.jwt-token.subject}")
    private String subject;

    /**
     * Метод для генерации JWT-токена.
     *
     * @param id ID пользователя, для которого нужно сгенерировать токен.
     * @return сгенерированный JWT-токен.
     */
    public String generateToken(UUID id) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(60).toInstant());

        String token = JWT.create()
                .withSubject(subject)
                .withClaim("id", id.toString())
                .withIssuedAt(new Date())
                .withIssuer(issuer)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));

        return token;
    }

    /**
     * Метод для проверки JWT-токена, который возвращает ID пользователя, указанный в токене.
     *
     * @param token JWT-токен.
     * @return список утверждений JWT-токена.
     * @throws JWTVerificationException если токен не прошел верификацию.
     */
    public List<String> validateTokenAndRetrieveClaim(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(issuer)
                .build();

        DecodedJWT decodedJWT = verifier.verify(token);

        String id = decodedJWT.getClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier").asString();
        String name = decodedJWT.getClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name").asString();
        String email = decodedJWT.getClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress").asString();
        String ban = decodedJWT.getClaim("ban").asString();
        List<String> roles = decodedJWT.getClaim("http://schemas.microsoft.com/ws/2008/06/identity/claims/role").asString() != null ?
                Collections.singletonList(decodedJWT.getClaim("http://schemas.microsoft.com/ws/2008/06/identity/claims/role").asString()) : decodedJWT.getClaim("http://schemas.microsoft.com/ws/2008/06/identity/claims/role").asList(String.class);

        List<String> claims = new ArrayList<>();
        claims.add(id);
        claims.add(name);
        claims.add(email);
        claims.add(ban);
        claims.add(roles.toString());

        return claims;
    }

}

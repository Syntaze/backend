package com.syntaze.backend.infra.jwt;

package com.syntaze.backend.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.syntaze.backend.application.port.TokenService;
import com.syntaze.backend.domain.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class JwtTokenService implements TokenService {

    private final Algorithm algorithm;
    private final long expirationMillis;

    public JwtTokenService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMillis
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationMillis = expirationMillis;
    }

    @Override
    public String generate(User user) {
        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("email", user.getEmail())
                .withClaim("role", user.getRole().name())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationMillis))
                .sign(algorithm);
    }

    @Override
    public String getSubject(String token) {
        return decode(token).getSubject();
    }

    @Override
    public boolean isValid(String token) {
        try {
            decode(token);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    @Override
    public Authentication getAuthentication(String token) {
        DecodedJWT jwt = decode(token);

        String userId = jwt.getSubject();
        String role = jwt.getClaim("role").asString();

        return new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

    private DecodedJWT decode(String token) {
        return JWT.require(algorithm)
                .build()
                .verify(token);
    }
}

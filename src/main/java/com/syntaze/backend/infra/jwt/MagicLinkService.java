package com.syntaze.backend.infra.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MagicLinkService {

    private final Algorithm algorithm;
    private final long expirationMillis;

    public MagicLinkService(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.magic.expiration:900000}") long expirationMillis) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationMillis = expirationMillis;
    }

    public String generateMagicToken(String email, String type) {
        return JWT.create()
                .withSubject(email)
                .withClaim("type", type)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationMillis))
                .sign(algorithm);
    }

    public boolean isValid(String token) {
        try {
            decode(token);
            return true;
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    public DecodedJWT decode(String token) {
        return JWT.require(algorithm).build().verify(token);
    }

}


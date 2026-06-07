package com.syntaze.backend.application.port;

import com.syntaze.backend.domain.model.User;
import org.springframework.security.core.Authentication;

public interface TokenService {

    String generate(User user);

    String getSubject(String token);

    boolean isValid(String token);

    Authentication getAuthentication(String token);
}
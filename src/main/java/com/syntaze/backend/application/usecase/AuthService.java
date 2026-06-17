package com.syntaze.backend.application.usecase;

import com.syntaze.backend.application.port.TokenService;
import com.syntaze.backend.domain.enums.Role;
import com.syntaze.backend.domain.model.User;
import com.syntaze.backend.infra.external.EmailService;
import com.syntaze.backend.infra.jwt.MagicLinkService;
import com.syntaze.backend.infra.repository.UserRepository;
import com.syntaze.backend.infra.repository.entity.UserEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final MagicLinkService magicLinkService;
    private final EmailService emailService;
    private final String appBaseUrl = "http://localhost:8080"; // adjust as needed or inject

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService,
                       MagicLinkService magicLinkService,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.magicLinkService = magicLinkService;
        this.emailService = emailService;
    }

    public void requestMagicLink(String email) {
        String token = magicLinkService.generateMagicToken(email, "magic");
        String link = appBaseUrl + "/auth/magic/confirm?token=" + token;
        emailService.sendMagicLink(email, link);
    }

    public Optional<String> confirmMagic(String token) {
        if (!magicLinkService.isValid(token)) return Optional.empty();

        String type = magicLinkService.decode(token).getClaim("type").asString();
        String email = magicLinkService.decode(token).getSubject();

        Optional<UserEntity> maybe = userRepository.findByEmail(email);
        if (maybe.isPresent()) {
            UserEntity entity = maybe.get();
            User domain = toDomain(entity);
            return Optional.of(tokenService.generate(domain));
        }

        if ("magic".equals(type)) {
            String registerToken = magicLinkService.generateMagicToken(email, "register");
            return Optional.of(registerToken);
        }

        return Optional.empty();
    }

    public Optional<String> registerWithPassword(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) return Optional.empty();

        UserEntity entity = new UserEntity();
        entity.setId(UUID.randomUUID());
        entity.setEmail(email);
        entity.setPassword(passwordEncoder.encode(password));
        entity.setRole(Role.USER);

        userRepository.save(entity);

        return Optional.of(tokenService.generate(toDomain(entity)));
    }

    public Optional<String> loginWithPassword(String email, String password) {
        Optional<UserEntity> maybe = userRepository.findByEmail(email);
        if (maybe.isEmpty()) return Optional.empty();
        UserEntity entity = maybe.get();
        if (entity.getPassword() == null) return Optional.empty();
        if (!passwordEncoder.matches(password, entity.getPassword())) return Optional.empty();
        return Optional.of(tokenService.generate(toDomain(entity)));
    }

    private User toDomain(UserEntity e) {
        User u = new User();
        u.setId(e.getId());
        u.setEmail(e.getEmail());
        u.setRole(e.getRole());
        return u;
    }
}


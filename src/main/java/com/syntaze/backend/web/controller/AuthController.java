package com.syntaze.backend.web.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.syntaze.backend.application.usecase.AuthService;
import com.syntaze.backend.infra.config.exception.ApiExceptions;
import com.syntaze.backend.infra.jwt.MagicLinkService;
import com.syntaze.backend.web.record.in.LoginRequestRecord;
import com.syntaze.backend.web.record.in.MagicRequestRecord;
import com.syntaze.backend.web.record.in.RegisterRequestRecord;
import com.syntaze.backend.web.record.out.MessageResponse;
import com.syntaze.backend.web.record.out.RegisterTokenResponse;
import com.syntaze.backend.web.record.out.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final MagicLinkService magicLinkService;

    public AuthController(AuthService authService, MagicLinkService magicLinkService) {
        this.authService = authService;
        this.magicLinkService = magicLinkService;
    }

    @PostMapping("/magic/request")
    public ResponseEntity<MessageResponse> requestMagic(@RequestBody MagicRequestRecord body) {
        String email = body.email();
        if (email == null || email.isBlank()) throw new ApiExceptions.BadRequestException("email is required");
        authService.requestMagicLink(email);
        return ResponseEntity.accepted().body(new MessageResponse("magic link sent if email exists"));
    }

    @GetMapping("/magic/confirm")
    public ResponseEntity<?> confirmMagic(@RequestParam("token") String token) {
        if (!magicLinkService.isValid(token)) throw new ApiExceptions.UnauthorizedException("invalid or expired token");

        DecodedJWT jwt = magicLinkService.decode(token);
        String type = jwt.getClaim("type").asString();

        var opt = authService.confirmMagic(token);
        if (opt.isEmpty()) throw new ApiExceptions.UnauthorizedException("invalid token");

        String resultToken = opt.get();
        if ("register".equals(type)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterTokenResponse(resultToken));
        }

        return ResponseEntity.ok(new TokenResponse(resultToken));
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequestRecord body) {
        String email = body.email();
        String password = body.password();
        String registerToken = body.registerToken();

        if (email == null || password == null) throw new ApiExceptions.BadRequestException("email and password required");

        if (registerToken != null) {
            if (!magicLinkService.isValid(registerToken)) throw new ApiExceptions.UnauthorizedException("invalid register token");
            DecodedJWT jwt = magicLinkService.decode(registerToken);
            if (!"register".equals(jwt.getClaim("type").asString()) || !email.equals(jwt.getSubject())) {
                throw new ApiExceptions.UnauthorizedException("invalid register token");
            }
        }

        var opt = authService.registerWithPassword(email, password);
        if (opt.isEmpty()) throw new ApiExceptions.ConflictException("user already exists");
        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponse(opt.get()));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequestRecord body) {
        String email = body.email();
        String password = body.password();
        if (email == null || password == null) throw new ApiExceptions.BadRequestException("email and password required");
        var opt = authService.loginWithPassword(email, password);
        if (opt.isEmpty()) throw new ApiExceptions.UnauthorizedException("invalid credentials");
        return ResponseEntity.ok(new TokenResponse(opt.get()));
    }

}


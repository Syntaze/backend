package com.syntaze.backend.infra.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConsoleEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailService.class);

    @Override
    public void sendMagicLink(String email, String link) {
        // Development-friendly: log magic link. Replace with real SMTP implementation later.
        log.info("[Magic Link] To: {} -> {}", email, link);
    }
}


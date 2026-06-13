package com.syntaze.backend.infra.external;

public interface EmailService {

    void sendMagicLink(String email, String link);

}


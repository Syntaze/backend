package com.syntaze.backend.domain.repository;

import com.syntaze.backend.domain.model.Bot;

import java.util.Optional;
import java.util.UUID;

public interface BotRepository {

    Bot save(Bot bot);

    Optional<Bot> findById(UUID id);

    Optional<Bot> findByApiKey(String apiKey);

    void deleteById(UUID id);

}


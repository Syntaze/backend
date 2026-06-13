package com.syntaze.backend.infra.repository;

import com.syntaze.backend.domain.model.Bot;
import com.syntaze.backend.domain.repository.BotRepository;
import com.syntaze.backend.infra.repository.entity.BotEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class BotPersistenceFacade implements BotRepository {

    private final BotJpaRepository jpa;

    public BotPersistenceFacade(BotJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Bot save(Bot bot) {
        BotEntity e = new BotEntity();
        e.setId(bot.getId() == null ? UUID.randomUUID() : bot.getId());
        e.setName(bot.getName());
        e.setApiKey(bot.getApiKey());
        e.setCreatedAt(bot.getCreatedAt() == null ? Instant.now() : bot.getCreatedAt());
        e.setLastSeenAt(bot.getLastSeenAt());

        BotEntity saved = jpa.save(e);
        Bot b = new Bot();
        b.setId(saved.getId());
        b.setName(saved.getName());
        b.setApiKey(saved.getApiKey());
        b.setCreatedAt(saved.getCreatedAt());
        b.setLastSeenAt(saved.getLastSeenAt());
        return b;
    }

    @Override
    public Optional<Bot> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Bot> findByApiKey(String apiKey) {
        return jpa.findByApiKey(apiKey).map(this::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    private Bot toDomain(BotEntity e) {
        Bot b = new Bot();
        b.setId(e.getId());
        b.setName(e.getName());
        b.setApiKey(e.getApiKey());
        b.setCreatedAt(e.getCreatedAt());
        b.setLastSeenAt(e.getLastSeenAt());
        return b;
    }
}


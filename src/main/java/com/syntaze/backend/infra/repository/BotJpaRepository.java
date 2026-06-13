package com.syntaze.backend.infra.repository;

import com.syntaze.backend.infra.repository.entity.BotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BotJpaRepository extends JpaRepository<BotEntity, UUID> {

    Optional<BotEntity> findByApiKey(String apiKey);

}


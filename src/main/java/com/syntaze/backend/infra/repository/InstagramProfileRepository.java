package com.syntaze.backend.infra.repository;

import com.syntaze.backend.infra.repository.entity.InstagramProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstagramProfileRepository extends JpaRepository<InstagramProfileEntity, UUID> {

    Optional<InstagramProfileEntity> findByUsername(String username);

}


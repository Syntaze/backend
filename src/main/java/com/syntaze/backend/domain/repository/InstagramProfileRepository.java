package com.syntaze.backend.domain.repository;

import com.syntaze.backend.domain.model.InstagramProfile;

import java.util.Optional;
import java.util.UUID;

public interface InstagramProfileRepository {

    InstagramProfile save(InstagramProfile profile);

    Optional<InstagramProfile> findProfileById(UUID id);

    Optional<InstagramProfile> findByUsername(String username);

    void deleteProfileById(UUID id);

}



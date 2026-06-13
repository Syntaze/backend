package com.syntaze.backend.domain.repository;

import com.syntaze.backend.domain.model.InstagramStory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InstagramStoryRepository {

    InstagramStory save(InstagramStory story);

    Optional<InstagramStory> findStoryById(UUID id);

    List<InstagramStory> findByProfileId(UUID profileId);

    void deleteStoryById(UUID id);

}




package com.syntaze.backend.infra.repository;

import com.syntaze.backend.domain.model.InstagramProfile;
import com.syntaze.backend.domain.model.InstagramStory;
import com.syntaze.backend.infra.repository.entity.InstagramProfileEntity;
import com.syntaze.backend.infra.repository.entity.InstagramStoryEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InstagramPersistenceFacade implements com.syntaze.backend.domain.repository.InstagramProfileRepository,
        com.syntaze.backend.domain.repository.InstagramStoryRepository {

    private final InstagramProfileRepository jpaProfileRepo;
    private final InstagramStoryRepository jpaStoryRepo;

    public InstagramPersistenceFacade(InstagramProfileRepository jpaProfileRepo, InstagramStoryRepository jpaStoryRepo) {
        this.jpaProfileRepo = jpaProfileRepo;
        this.jpaStoryRepo = jpaStoryRepo;
    }

    // ---- Profile operations ----
    @Override
    public InstagramProfile save(InstagramProfile profile) {
        InstagramProfileEntity entity = profileToEntity(profile);
        if (entity.getCreatedAt() == null) entity.setCreatedAt(Instant.now());
        entity.setLastUpdatedAt(Instant.now());
        InstagramProfileEntity saved = jpaProfileRepo.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<InstagramProfile> findProfileById(UUID id) {
        return jpaProfileRepo.findById(id).map(InstagramProfileEntity::toDomain);
    }

    @Override
    public Optional<InstagramProfile> findByUsername(String username) {
        return jpaProfileRepo.findByUsername(username).map(InstagramProfileEntity::toDomain);
    }

    @Override
    public void deleteProfileById(UUID id) {
        jpaProfileRepo.deleteById(id);
    }

    // ---- Story operations ----
    @Override
    public InstagramStory save(InstagramStory story) {
        InstagramStoryEntity entity = storyToEntity(story);
        if (entity.getCreatedAt() == null) entity.setCreatedAt(Instant.now());
        entity.setLastUpdatedAt(Instant.now());

        // ensure profile exists and set relation
        if (story.getProfileId() != null) {
            Optional<InstagramProfileEntity> p = jpaProfileRepo.findById(story.getProfileId());
            p.ifPresent(entity::setProfile);
        }

        InstagramStoryEntity saved = jpaStoryRepo.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<InstagramStory> findStoryById(UUID id) {
        return jpaStoryRepo.findById(id).map(InstagramStoryEntity::toDomain);
    }

    @Override
    public List<InstagramStory> findByProfileId(UUID profileId) {
        return jpaStoryRepo.findByProfileId(profileId).stream().map(InstagramStoryEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteStoryById(UUID id) {
        jpaStoryRepo.deleteById(id);
    }

    // ---- mapping helpers ----
    private InstagramProfileEntity profileToEntity(InstagramProfile p) {
        InstagramProfileEntity e = new InstagramProfileEntity();
        e.setId(p.getId() == null ? UUID.randomUUID() : p.getId());
        e.setUsername(p.getUsername());
        e.setFullName(p.getFullName());
        e.setFollowersCount(p.getFollowersCount());
        e.setFollowingCount(p.getFollowingCount());
        e.setStoriesPostedCount(p.getStoriesPostedCount());
        e.setCreatedAt(p.getCreatedAt());
        e.setLastUpdatedAt(p.getLastUpdatedAt());
        // stories managed separately
        return e;
    }

    private InstagramStoryEntity storyToEntity(InstagramStory s) {
        InstagramStoryEntity e = new InstagramStoryEntity();
        e.setId(s.getId() == null ? UUID.randomUUID() : s.getId());
        e.setExternalId(s.getExternalId());
        e.setLikes(s.getLikes());
        e.setPostedAt(s.getPostedAt());
        e.setCreatedAt(s.getCreatedAt());
        e.setLastUpdatedAt(s.getLastUpdatedAt());
        e.setScreenshotLink(s.getScreenshotLink());
        // profile is set above when saving
        return e;
    }

}




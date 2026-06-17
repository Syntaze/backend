package com.syntaze.backend.infra.repository;

import com.syntaze.backend.infra.repository.entity.InstagramStoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InstagramStoryRepository extends JpaRepository<InstagramStoryEntity, UUID> {

    List<InstagramStoryEntity> findByProfileId(UUID profileId);

}

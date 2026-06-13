package com.syntaze.backend.web.record.out;

import java.time.Instant;
import java.util.UUID;

public record StoryOut(UUID id, UUID profileId, String externalId, Integer likes, Instant postedAt, Instant createdAt, Instant lastUpdatedAt) {
}


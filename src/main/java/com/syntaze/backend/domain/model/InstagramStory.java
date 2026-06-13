package com.syntaze.backend.domain.model;

import java.time.Instant;
import java.util.UUID;

public class InstagramStory {

    private UUID id;
    private UUID profileId;
    private String externalId;
    private Integer likes;
    private Instant postedAt;
    private Instant createdAt;
    private Instant lastUpdatedAt;
    private String screenshotLink;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Instant getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(Instant postedAt) {
        this.postedAt = postedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Instant lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getScreenshotLink() {
        return screenshotLink;
    }

    public void setScreenshotLink(String screenshotLink) {
        this.screenshotLink = screenshotLink;
    }
}



package com.syntaze.backend.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class InstagramProfile {

    private UUID id;
    private String username;
    private String fullName;
    private Integer followersCount;
    private Integer followingCount;
    private Integer storiesPostedCount;
    private Instant createdAt;
    private Instant lastUpdatedAt;
    private List<InstagramStory> stories;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public Integer getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }

    public Integer getStoriesPostedCount() {
        return storiesPostedCount;
    }

    public void setStoriesPostedCount(Integer storiesPostedCount) {
        this.storiesPostedCount = storiesPostedCount;
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

    public List<InstagramStory> getStories() {
        return stories;
    }

    public void setStories(List<InstagramStory> stories) {
        this.stories = stories;
    }
}


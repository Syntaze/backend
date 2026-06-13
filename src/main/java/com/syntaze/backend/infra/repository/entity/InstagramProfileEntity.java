package com.syntaze.backend.infra.repository.entity;

import com.syntaze.backend.domain.model.InstagramProfile;
import com.syntaze.backend.domain.model.InstagramStory;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "instagram_profiles")
public class InstagramProfileEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String fullName;

    @Column
    private Integer followersCount;

    @Column
    private Integer followingCount;

    @Column
    private Integer storiesPostedCount;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant lastUpdatedAt;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InstagramStoryEntity> stories = new ArrayList<>();

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

    public List<InstagramStoryEntity> getStories() {
        return stories;
    }

    public void setStories(List<InstagramStoryEntity> stories) {
        this.stories = stories;
    }

    public InstagramProfile toDomain() {
        InstagramProfile p = new InstagramProfile();
        p.setId(this.id);
        p.setUsername(this.username);
        p.setFullName(this.fullName);
        p.setFollowersCount(this.followersCount);
        p.setFollowingCount(this.followingCount);
        p.setStoriesPostedCount(this.storiesPostedCount);
        p.setCreatedAt(this.createdAt);
        p.setLastUpdatedAt(this.lastUpdatedAt);
        p.setStories(this.stories.stream().map(InstagramStoryEntity::toDomain).collect(Collectors.toList()));
        return p;
    }

}



package com.syntaze.backend.infra.repository.entity;

import com.syntaze.backend.domain.model.InstagramStory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "instagram_stories")
public class InstagramStoryEntity {

    @Id
    private UUID id;

    @Column
    private String externalId;

    @Column
    private Integer likes;

    @Column
    private Instant postedAt;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant lastUpdatedAt;

    @Column
    private String screenshotLink;

    @ManyToOne(fetch = FetchType.LAZY)
    private InstagramProfileEntity profile;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public InstagramProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(InstagramProfileEntity profile) {
        this.profile = profile;
    }

    public InstagramStory toDomain() {
        InstagramStory s = new InstagramStory();
        s.setId(this.id);
        s.setExternalId(this.externalId);
        s.setLikes(this.likes);
        s.setPostedAt(this.postedAt);
        s.setCreatedAt(this.createdAt);
        s.setLastUpdatedAt(this.lastUpdatedAt);
        s.setScreenshotLink(this.screenshotLink);
        return s;
    }

}



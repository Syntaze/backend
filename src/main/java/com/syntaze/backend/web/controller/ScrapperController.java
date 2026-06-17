package com.syntaze.backend.web.controller;

import com.syntaze.backend.domain.model.InstagramProfile;
import com.syntaze.backend.domain.model.InstagramStory;
import com.syntaze.backend.infra.repository.InstagramPersistenceFacade;
import com.syntaze.backend.web.record.in.ScrapeRequestRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/scrapper")
public class ScrapperController {

    private final InstagramPersistenceFacade persistence;

    public ScrapperController(InstagramPersistenceFacade persistence) {
        this.persistence = persistence;
    }

    @PostMapping("/profile-result")
    public ResponseEntity<?> receiveProfileResult(@RequestBody ScrapeRequestRecord req) {
        if (req == null || req.profile() == null) return ResponseEntity.badRequest().body("missing profile");

        ScrapeRequestRecord.ProfileRecord p = req.profile();

        InstagramProfile profile = new InstagramProfile();
        try {
            if (p.id() != null && !p.id().isBlank()) profile.setId(java.util.UUID.fromString(p.id()));
        } catch (IllegalArgumentException ex) {
            // ignore invalid uuid
        }
        profile.setUsername(p.username());
        profile.setFullName(p.fullName());
        profile.setFollowersCount(p.followersCount());
        profile.setFollowingCount(p.followingCount());
        profile.setCreatedAt(Instant.now());
        profile.setLastUpdatedAt(Instant.now());

        InstagramProfile savedProfile = persistence.save(profile);

        List<ScrapeRequestRecord.PostRecord> posts = req.posts();
        if (posts != null) {
            for (ScrapeRequestRecord.PostRecord pr : posts) {
                InstagramStory s = new InstagramStory();
                s.setId(null); // persistence will generate id
                s.setProfileId(savedProfile.getId());
                s.setExternalId(pr.externalId());
                s.setLikes(pr.likes() == null ? 0 : pr.likes());
                try {
                    if (pr.timestamp() != null) s.setPostedAt(Instant.parse(pr.timestamp()));
                } catch (DateTimeParseException ex) {
                    s.setPostedAt(Instant.now());
                }
                s.setCreatedAt(Instant.now());
                s.setLastUpdatedAt(Instant.now());
                s.setScreenshotLink(pr.thumbnailUrl());

                persistence.save(s);
            }
        }

        return ResponseEntity.ok().body("ok");
    }

}


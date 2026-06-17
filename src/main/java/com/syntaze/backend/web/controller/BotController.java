package com.syntaze.backend.web.controller;

import com.syntaze.backend.domain.model.InstagramStory;
import com.syntaze.backend.domain.model.InstagramProfile;
import com.syntaze.backend.domain.repository.BotRepository;
import com.syntaze.backend.infra.repository.InstagramPersistenceFacade;
import com.syntaze.backend.infra.external.FileStorageService;
import com.syntaze.backend.infra.config.exception.ApiExceptions;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

@RestController
public class BotController {

    private final BotRepository botRepository;
    private final InstagramPersistenceFacade persistence;
    private final FileStorageService storage;

    public BotController(BotRepository botRepository, InstagramPersistenceFacade persistence, FileStorageService storage) {
        this.botRepository = botRepository;
        this.persistence = persistence;
        this.storage = storage;
    }

    @PostMapping(path = "/bots/register")
    public ResponseEntity<?> registerBot(@org.springframework.web.bind.annotation.RequestBody(required = false) java.util.Map<String, String> body) {
        String name = "scrapper";
        if (body != null && body.get("name") != null && !body.get("name").isBlank()) name = body.get("name");

        com.syntaze.backend.domain.model.Bot bot = new com.syntaze.backend.domain.model.Bot();
        bot.setId(java.util.UUID.randomUUID());
        bot.setName(name);
        String apiKey = java.util.UUID.randomUUID().toString();
        bot.setApiKey(apiKey);
        bot.setCreatedAt(Instant.now());

        var saved = botRepository.save(bot);

        return ResponseEntity.ok(java.util.Map.of("apiKey", saved.getApiKey()));
    }

    @PostMapping(path = "/bots/data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> receiveData(
            @RequestHeader("X-BOT-API-KEY") String apiKey,
            @RequestPart(required = false) String profileId,
            @RequestPart(required = false) String externalId,
            @RequestPart(required = false) Integer likes,
            @RequestPart(required = false) String postedAt,
            @RequestPart MultipartFile screenshot
    ) {
        var maybeBot = botRepository.findByApiKey(apiKey);
        if (maybeBot.isEmpty()) throw new ApiExceptions.UnauthorizedException("invalid bot api key");

        UUID profileUuid = null;
        if (profileId != null && !profileId.isBlank()) profileUuid = UUID.fromString(profileId);

        String screenshotLink;
        try {
            screenshotLink = storage.store(screenshot);
        } catch (Exception ex) {
            throw new ApiExceptions.BadRequestException("failed to store screenshot: " + ex.getMessage());
        }

        InstagramStory story = new InstagramStory();
        story.setId(UUID.randomUUID());
        story.setExternalId(externalId);
        story.setLikes(likes == null ? 0 : likes);
        story.setPostedAt(postedAt == null ? Instant.now() : Instant.parse(postedAt));
        story.setCreatedAt(Instant.now());
        story.setLastUpdatedAt(Instant.now());
        story.setScreenshotLink(screenshotLink);
        story.setProfileId(profileUuid);

        // if profileId provided ensure profile exists
        if (profileUuid != null) {
            var profileOpt = persistence.findProfileById(profileUuid);
            if (profileOpt.isEmpty()) {
                // create minimal profile stub
                InstagramProfile p = new InstagramProfile();
                p.setId(profileUuid);
                p.setUsername("unknown");
                p.setCreatedAt(Instant.now());
                p.setLastUpdatedAt(Instant.now());
                persistence.save(p);
            }
        }

        var saved = persistence.save(story);

        return ResponseEntity.ok().body(saved);
    }
}

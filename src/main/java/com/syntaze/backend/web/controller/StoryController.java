package com.syntaze.backend.web.controller;

import com.syntaze.backend.domain.model.InstagramStory;
import com.syntaze.backend.infra.repository.InstagramPersistenceFacade;
import com.syntaze.backend.web.record.out.StoryOut;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

@RestController
@RequestMapping("/stories")
public class StoryController {

    private final InstagramPersistenceFacade persistence;

    public StoryController(InstagramPersistenceFacade persistence) {
        this.persistence = persistence;
    }

    @GetMapping
    public ResponseEntity<Page<StoryOut>> listStories(Pageable pageable) {
        Page<InstagramStory> page = persistence.findAll(pageable);
        Page<StoryOut> out = page.map(s -> new StoryOut(s.getId(), s.getProfileId(), s.getExternalId(), s.getLikes(), s.getPostedAt(), s.getCreatedAt(), s.getLastUpdatedAt()));
        return ResponseEntity.ok(out);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryOut> getStory(@PathVariable("id") UUID id) {
        var opt = persistence.findStoryById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        InstagramStory s = opt.get();
        StoryOut out = new StoryOut(s.getId(), s.getProfileId(), s.getExternalId(), s.getLikes(), s.getPostedAt(), s.getCreatedAt(), s.getLastUpdatedAt());
        return ResponseEntity.ok(out);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> downloadImage(@PathVariable("id") UUID id) throws Exception {
        var opt = persistence.findStoryById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        InstagramStory s = opt.get();
        String path = s.getScreenshotLink();
        if (path == null || path.isBlank()) return ResponseEntity.notFound().build();

        File file = new File(path);
        if (!file.exists()) return ResponseEntity.notFound().build();

        Resource resource = new FileSystemResource(file);
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        HttpHeaders headers = new HttpHeaders();
        ContentDisposition cd = ContentDisposition.attachment().filename(file.getName()).build();
        headers.setContentDisposition(cd);

        return ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(MediaType.parseMediaType(contentType)).body(resource);
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<StoryOut> downloadInfo(@PathVariable("id") UUID id) {
        var opt = persistence.findStoryById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        InstagramStory s = opt.get();
        StoryOut out = new StoryOut(s.getId(), s.getProfileId(), s.getExternalId(), s.getLikes(), s.getPostedAt(), s.getCreatedAt(), s.getLastUpdatedAt());

        HttpHeaders headers = new HttpHeaders();
        ContentDisposition cd = ContentDisposition.attachment().filename("story-" + id + ".json").build();
        headers.setContentDisposition(cd);

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_JSON).body(out);
    }
}



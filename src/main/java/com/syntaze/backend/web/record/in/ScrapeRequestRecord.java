package com.syntaze.backend.web.record.in;

import java.util.List;

public record ScrapeRequestRecord(ProfileRecord profile, List<PostRecord> posts) {

    public record ProfileRecord(String id, String username, String fullName, String profilePicUrl, Boolean isPrivate, Boolean isVerified, Integer followersCount, Integer followingCount) {
    }

    public record PostRecord(String externalId, String timestamp, String caption, String mediaType, String thumbnailUrl, Integer likes, Integer comments) {
    }

}


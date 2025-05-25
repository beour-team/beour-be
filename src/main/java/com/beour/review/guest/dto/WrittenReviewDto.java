package com.beour.review.guest.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WrittenReviewDto {

    private String spaceName;
    private String thumbnailUrl;
    private int rating;
    private String nickname;
    private String reviewDate;
    private String reservedDate;
    private String comment;

    private HostCommentDto hostComment;

    @Getter
    @Builder
    public static class HostCommentDto {
        private String nickname;
        private String content;
        private String createdAt;
    }
}

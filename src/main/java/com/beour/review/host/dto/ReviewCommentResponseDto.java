package com.beour.review.host.dto;

import com.beour.review.domain.entity.Review;
import com.beour.review.domain.entity.ReviewComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCommentResponseDto {

    private String guestNickname;
    private int reviewRating;
    private LocalDateTime reviewCreatedAt;
    private String spaceName;
    private LocalDate reservationDate;
    private String reviewContent;
    private List<String> reviewImages;
    private Long reviewCommentId;
    private String hostNickname;
    private LocalDateTime reviewCommentCreatedAt;
    private String reviewCommentContent;

    public static ReviewCommentResponseDto of(Review review, ReviewComment reviewComment) {
        return ReviewCommentResponseDto.builder()
                .guestNickname(review.getGuest().getNickname())
                .reviewRating(review.getRating())
                .reviewCreatedAt(review.getCreatedAt())
                .spaceName(review.getSpace().getName())
                .reservationDate(review.getReservedDate())
                .reviewContent(review.getContent())
                .reviewImages(review.getImages().stream()
                        .map(reviewImage -> reviewImage.getImageUrl())
                        .collect(Collectors.toList()))
                .reviewCommentId(reviewComment.getId())
                .hostNickname(reviewComment.getUser().getNickname())
                .reviewCommentCreatedAt(reviewComment.getCreatedAt())
                .reviewCommentContent(reviewComment.getContent())
                .build();
    }
}

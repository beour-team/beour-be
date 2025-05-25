package com.beour.review.guest.controller;

import com.beour.review.guest.dto.ReviewCreateRequestDto;
import com.beour.review.guest.dto.ReviewableSpaceDto;
import com.beour.review.guest.dto.WrittenReviewDto;
import com.beour.review.guest.service.GuestReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class GuestReviewController {

    private final GuestReviewService guestReviewService;

    @GetMapping("/reviewable")
    public ResponseEntity<List<ReviewableSpaceDto>> getReviewableSpaces(@RequestParam Long guestId) {
        List<ReviewableSpaceDto> result = guestReviewService.getReviewableSpaces(guestId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/written")
    public ResponseEntity<List<WrittenReviewDto>> getMyReviews(@RequestParam Long guestId) {
        List<WrittenReviewDto> reviews = guestReviewService.getWrittenReviews(guestId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<Void> createReview(
            @RequestHeader("X-USER-ID") Long guestId, // 또는 SecurityContext 사용
            @RequestBody ReviewCreateRequestDto request) {

        guestReviewService.createReview(guestId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId,
                                             @RequestHeader("guestId") Long guestId) {
        guestReviewService.deleteReview(guestId, reviewId);
        return ResponseEntity.noContent().build();
    }
}

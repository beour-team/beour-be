package com.beour.review.guest.service;

import com.beour.reservation.commons.entity.Reservation;
import com.beour.reservation.commons.enums.ReservationStatus;
import com.beour.reservation.commons.repository.ReservationRepository;
import com.beour.review.domain.entity.Review;
import com.beour.review.domain.entity.ReviewImage;
import com.beour.review.domain.repository.ReviewImageRepository;
import com.beour.review.domain.repository.ReviewRepository;
import com.beour.review.guest.dto.ReviewCreateRequestDto;
import com.beour.review.guest.dto.ReviewUpdateRequestDto;
import com.beour.review.guest.dto.ReviewableSpaceDto;
import com.beour.review.guest.dto.WrittenReviewDto;
import com.beour.space.domain.entity.Space;
import com.beour.space.domain.repository.SpaceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestReviewService {

    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final SpaceRepository spaceRepository;

    public List<ReviewableSpaceDto> getReviewableSpaces(Long guestId) {
        return reservationRepository.findByUserIdAndStatusAndDeletedAtIsNull(guestId, ReservationStatus.COMPLETED)
                .stream()
                .filter(res -> !reviewRepository.existsByReservationId(res.getId()))
                .map(res -> ReviewableSpaceDto.builder()
                        .reservationId(res.getId())
                        .spaceName(res.getSpace().getName())
                        .date(res.getDate().toString())
                        .startTime(res.getStartTime().toString())
                        .endTime(res.getEndTime().toString())
                        .guestCount(res.getGuestCount())
                        .thumbnailUrl(res.getSpace().getThumbnailUrl())
                        .build()
                ).toList();
    }

    public List<WrittenReviewDto> getWrittenReviews(Long guestId) {
        return reviewRepository.findByGuestIdAndDeletedAtIsNull(guestId).stream()
                .map(review -> WrittenReviewDto.builder()
                        .spaceName(review.getSpace().getName())
                        .thumbnailUrl(review.getSpace().getThumbnailUrl())
                        .rating(review.getRating())
                        .nickname(review.getGuest().getNickname())
                        .reviewDate(review.getCreatedAt().toLocalDate().toString())
                        .guestContent(review.getContent())
                        .hostComment(review.getHostComment() != null ?
                                WrittenReviewDto.HostCommentDto.builder()
                                        .nickname(review.getHostComment().getHost().getNickname())
                                        .hostContent(review.getHostComment().getContent())
                                        .createdAt(review.getHostComment().getCreatedAt().toLocalDate().toString())
                                        .build() : null)
                        .build())
                .toList();
    }

    @Transactional
    public void createReview(Long guestId, ReviewCreateRequestDto request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        if (reservation.getGuest().getId() != guestId) {
            throw new IllegalArgumentException("해당 예약에 대한 작성 권한이 없습니다.");
        }

        if (reviewRepository.existsByReservationId(reservation.getId())) {
            throw new IllegalStateException("이미 리뷰가 작성된 예약입니다.");
        }

        Space space = reservation.getSpace();

        Review review = Review.builder()
                .reservation(reservation)
                .space(space)
                .guest(reservation.getGuest())
                .rating(request.getRating())
                .content(request.getContent())
                .build();

        reviewRepository.save(review);

        if (request.getImageUrls() != null) {
            request.getImageUrls().forEach(url -> {
                ReviewImage image = ReviewImage.builder()
                        .review(review)
                        .imageUrl(url)
                        .build();
                reviewImageRepository.save(image);
            });
        }

        updateAvgRatingFromDb(space);
    }

    private void updateAvgRatingFromDb(Space space) {
        Double avgRating = reviewRepository.findAverageRatingBySpaceId(space.getId());
        space.updateAvgRating(avgRating);
        spaceRepository.save(space);
    }

    @Transactional
    public void updateReview(Long guestId, Long reviewId, ReviewUpdateRequestDto request) {
        Review review = reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("해당 리뷰를 찾을 수 없습니다."));

        if (review.getGuest().getId() != guestId) {
            throw new AccessDeniedException("리뷰를 수정할 권한이 없습니다.");
        }

        reviewImageRepository.deleteByReviewId(reviewId); // 기존 이미지 삭제

        if (request.getImageUrls() != null) {
            List<ReviewImage> newImages = request.getImageUrls().stream()
                    .map(url -> ReviewImage.builder()
                            .review(review)
                            .imageUrl(url)
                            .build())
                    .toList();
            reviewImageRepository.saveAll(newImages);
        }

        review.updateRating(request.getRating());
        review.updateContent(request.getContent());

        recalculateAvgRatingFromReviews(review.getSpace());
    }

    private void recalculateAvgRatingFromReviews(Space space) {
        List<Review> reviews = reviewRepository.findBySpaceIdAndDeletedAtIsNull(space.getId());
        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        space.updateAvgRating(average);
    }
}

package com.beour.review.guest.service;

import com.beour.reservation.commons.enums.ReservationStatus;
import com.beour.reservation.commons.repository.ReservationRepository;
import com.beour.review.domain.entity.ReviewComment;
import com.beour.review.domain.repository.ReviewRepository;
import com.beour.review.guest.dto.ReviewableSpaceDto;
import com.beour.review.guest.dto.WrittenReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestReviewService {

    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;

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
                        .reservedDate(review.getReservedDate().toString())
                        .comment(review.getContent())
                        .hostComment(review.getHostComment() != null ?
                                WrittenReviewDto.HostCommentDto.builder()
                                        .nickname(review.getHostComment().getHost().getNickname())
                                        .content(review.getHostComment().getContent())
                                        .createdAt(review.getHostComment().getCreatedAt().toLocalDate().toString())
                                        .build() : null)
                        .build())
                .toList();
    }

}

package com.beour.review.domain.repository;

import com.beour.review.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByReservationId(Long reservationId);
    List<Review> findByGuestIdAndDeletedAtIsNull(Long guestId);
    Optional<Review> findByGuestIdAndSpaceIdAndReservedDateAndDeletedAtIsNull(Long guestId, Long spaceId, LocalDate reservedDate);
}

package com.beour.review.domain.repository;

import com.beour.review.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByReservationId(Long reservationId);
    List<Review> findByGuestIdAndDeletedAtIsNull(Long guestId);
    Optional<Review> findByGuestIdAndSpaceIdAndReservedDateAndDeletedAtIsNull(Long guestId, Long spaceId, LocalDate reservedDate);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.space.id = :spaceId AND r.deletedAt IS NULL")
    Double findAverageRatingBySpaceId(@Param("spaceId") Long spaceId);
}

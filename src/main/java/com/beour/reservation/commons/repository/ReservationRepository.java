package com.beour.reservation.commons.repository;

import com.beour.reservation.commons.entity.Reservation;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findBySpaceIdAndDateAndDeletedAtIsNull(Long spaceId, LocalDate date);

    @Query("SELECT r FROM Reservation r " +
        "WHERE r.guest.id = :guestId AND " +
        "(r.date > :today OR (r.date = :today AND r.startTime > :now))")
    List<Reservation> findUpcomingReservationsByGuest(
        @Param("guestId") Long guestId,
        @Param("today") LocalDate today,
        @Param("now") LocalTime now
    );

    @Query("SELECT r FROM Reservation r " +
        "WHERE r.guest.id = :guestId AND " +
        "(r.date < :today OR (r.date = :today AND r.endTime <= :now))")
    List<Reservation> findPastReservationsByGuest(
        @Param("guestId") Long guestId,
        @Param("today") LocalDate today,
        @Param("now") LocalTime now
    );


}

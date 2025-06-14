package com.beour.reservation.commons.entity;

import com.beour.global.entity.BaseTimeEntity;
import com.beour.reservation.commons.enums.ReservationStatus;
import com.beour.space.domain.entity.Space;
import com.beour.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id")
    private User guest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private User host;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int price;
    private int guestCount;

    public void cancel(){
        this.status = ReservationStatus.REJECTED;
        this.softDelete();
    }

    public void updateStatus(ReservationStatus status){
        this.status = status;
    }

    @Builder
    private Reservation(User guest, User host, Space space, ReservationStatus status, LocalDate date, LocalTime startTime, LocalTime endTime, int price, int guestCount){
        this.guest = guest;
        this.host = host;
        this.space = space;
        this.status = status;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.guestCount = guestCount;
    }
}

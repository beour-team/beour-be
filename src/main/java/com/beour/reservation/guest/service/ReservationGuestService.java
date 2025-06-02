package com.beour.reservation.guest.service;

import com.beour.global.exception.exceptionType.SpaceNotFoundException;
import com.beour.global.exception.exceptionType.UserNotFoundException;
import com.beour.reservation.commons.entity.Reservation;
import com.beour.reservation.commons.enums.ReservationStatus;
import com.beour.reservation.commons.exceptionType.AvailableTimeNotFound;
import com.beour.reservation.commons.exceptionType.ReservationNotFound;
import com.beour.reservation.commons.repository.ReservationRepository;
import com.beour.reservation.guest.dto.CheckAvailableTimesRequestDto;
import com.beour.reservation.guest.dto.ReservationCreateRequest;
import com.beour.reservation.guest.dto.ReservationListResponseDto;
import com.beour.reservation.guest.dto.ReservationResponseDto;
import com.beour.space.domain.entity.AvailableTime;
import com.beour.space.domain.entity.Space;
import com.beour.space.domain.repository.SpaceRepository;
import com.beour.user.entity.User;
import com.beour.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReservationGuestService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final SpaceRepository spaceRepository;
    private final CheckAvailableTimeService checkAvailableTimeService;

    public ReservationResponseDto createReservation(ReservationCreateRequest requestDto) {
        User guest = getUser(requestDto.getGuestId());
        User host = getUser(requestDto.getHostId());
        Space space = spaceRepository.findById(requestDto.getSpaceId()).orElseThrow(
            () -> new SpaceNotFoundException("존재하지 않는 공간입니다.")
        );

        checkReservationAvailable(requestDto);

        Reservation reservation = Reservation.builder()
            .guest(guest)
            .host(host)
            .space(space)
            .status(ReservationStatus.PENDING)
            .date(requestDto.getDate())
            .startTime(requestDto.getStartTime())
            .endTime(requestDto.getEndTime())
            .price(requestDto.getPrice())
            .guestCount(requestDto.getGuestCount())
            .build();

        return ReservationResponseDto.of(reservationRepository.save(reservation));
    }

    private void checkReservationAvailable(ReservationCreateRequest requestDto) {
        checkReservationAvailableDate(requestDto);
        checkReservationAvailabeTime(requestDto);
    }

    private void checkReservationAvailabeTime(ReservationCreateRequest requestDto) {
        List<Reservation> reservationList = reservationRepository.findBySpaceIdAndDateAndDeletedAtIsNull(
            requestDto.getSpaceId(), requestDto.getDate());

        LocalTime startTime = requestDto.getStartTime();
        while (startTime.isBefore(requestDto.getEndTime())) {
            LocalTime currentTime = requestDto.getStartTime();

            boolean isReserved = reservationList.stream().anyMatch(reservation ->
                reservation.getStartTime().isBefore(currentTime.plusHours(1)) &&
                    reservation.getEndTime().isAfter(currentTime)
            );

            if (isReserved) {
                throw new AvailableTimeNotFound("예약이 불가능한 시간입니다.");
            }

            startTime = startTime.plusHours(1);
        }
    }

    private void checkReservationAvailableDate(ReservationCreateRequest requestDto) {
        AvailableTime availableTime = checkAvailableTimeService.checkReservationAvailableDateAndGetAvailableTime(
            new CheckAvailableTimesRequestDto(
                requestDto.getSpaceId(), requestDto.getDate()));
        if (availableTime.getStartTime().isAfter(requestDto.getStartTime())
            || availableTime.getEndTime().isBefore(
            requestDto.getEndTime())) {
            throw new AvailableTimeNotFound("예약이 불가능한 시간입니다.");
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new UserNotFoundException("존재하지 않는 유저입니다.")
        );
    }

    public List<ReservationListResponseDto> findReservationList(Long guestId) {
        List<Reservation> reservationList = reservationRepository.findUpcomingReservationsByGuest(
            guestId, LocalDate.now(), LocalTime.now());

        checkEmptyReservation(reservationList);

        List<ReservationListResponseDto> responseDtoList = new ArrayList<>();
        for (Reservation reservation : reservationList) {
            responseDtoList.add(ReservationListResponseDto.of(reservation));
        }

        return responseDtoList;
    }

    public List<ReservationListResponseDto> findPastReservationList(Long guestId) {
        List<Reservation> reservationList = reservationRepository.findPastReservationsByGuest(
            guestId, LocalDate.now(), LocalTime.now());

        checkEmptyReservation(reservationList);

        List<ReservationListResponseDto> responseDtoList = new ArrayList<>();
        for (Reservation reservation : reservationList) {
            responseDtoList.add(ReservationListResponseDto.of(reservation));
        }

        return responseDtoList;
    }

    private static void checkEmptyReservation(List<Reservation> reservationList) {
        if (reservationList.isEmpty()) {
            throw new ReservationNotFound("예약이 없습니다.");
        }
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
            () -> new ReservationNotFound("해당 예약이 존재하지 않습니다.")
        );

        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new IllegalStateException("이미 완료된 예약은 취소할 수 없습니다.");
        }

        reservation.cancel();
    }
}

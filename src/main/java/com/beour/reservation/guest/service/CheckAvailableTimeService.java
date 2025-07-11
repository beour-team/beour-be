package com.beour.reservation.guest.service;

import com.beour.global.exception.error.errorcode.AvailableTimeErrorCode;
import com.beour.reservation.commons.entity.Reservation;
import com.beour.reservation.commons.enums.ReservationStatus;
import com.beour.reservation.commons.exceptionType.AvailableTimeNotFound;
import com.beour.reservation.commons.repository.ReservationRepository;
import com.beour.reservation.guest.dto.CheckAvailableTimesRequestDto;
import com.beour.reservation.guest.dto.SpaceAvailableTimeResponseDto;
import com.beour.space.domain.entity.AvailableTime;
import com.beour.space.domain.repository.AvailableTimeRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CheckAvailableTimeService {

    private final AvailableTimeRepository availableTimeRepository;
    private final ReservationRepository reservationRepository;

    public SpaceAvailableTimeResponseDto findAvailableTime(
        CheckAvailableTimesRequestDto requestDto) {
        AvailableTime availableTime = checkReservationAvailableDateAndGetAvailableTime(requestDto);

        List<Reservation> reservationList = reservationRepository.findBySpaceIdAndDateAndStatusNot(
            requestDto.getSpaceId(), requestDto.getDate(), ReservationStatus.REJECTED);

        List<LocalTime> findTimeList = getAvailableTimeList(availableTime, reservationList,
            requestDto.getDate());
        if (findTimeList.isEmpty()) {
            throw new AvailableTimeNotFound(AvailableTimeErrorCode.AVAILABLE_TIME_NOT_FOUND);
        }

        return SpaceAvailableTimeResponseDto.of(findTimeList);
    }

    public AvailableTime checkReservationAvailableDateAndGetAvailableTime(
        CheckAvailableTimesRequestDto requestDto) {
        if (requestDto.getDate().isBefore(LocalDate.now())) {
            throw new AvailableTimeNotFound(AvailableTimeErrorCode.AVAILABLE_TIME_NOT_FOUND);
        }

        return availableTimeRepository.findBySpaceIdAndDateAndDeletedAtIsNull(
            requestDto.getSpaceId(), requestDto.getDate()).orElseThrow(
            () -> new AvailableTimeNotFound(AvailableTimeErrorCode.AVAILABLE_TIME_NOT_FOUND)
        );
    }

    private static List<LocalTime> getAvailableTimeList(AvailableTime availableTime,
        List<Reservation> reservationList, LocalDate date) {
        LocalTime startTime = availableTime.getStartTime();

        if (Objects.equals(date, LocalDate.now())) {
            startTime = LocalTime.of(LocalTime.now().getHour() + 1, 0);
        }

        LocalTime endTime = availableTime.getEndTime();

        if (reservationList.isEmpty()) {
            return getAvailableTimeWithoutReservation(startTime, endTime);
        }

        return getAvailableTimeWithReservation(reservationList, startTime, endTime);
    }

    private static List<LocalTime> getAvailableTimeWithReservation(
        List<Reservation> reservationList, LocalTime startTime, LocalTime endTime) {

        List<LocalTime> list = new ArrayList<>();

        while (startTime.isBefore(endTime)) {
            LocalTime currentTime = startTime;

            boolean isReserved = reservationList.stream().anyMatch(reservation ->
                reservation.getStartTime().isBefore(currentTime.plusHours(1)) &&
                    reservation.getEndTime().isAfter(currentTime)
            );

            if (!isReserved) {
                list.add(startTime);
            }

            startTime = startTime.plusHours(1);
        }

        return list;
    }

    private static List<LocalTime> getAvailableTimeWithoutReservation(LocalTime startTime,
        LocalTime endTime) {

        List<LocalTime> list = new ArrayList<>();

        while (startTime.isBefore(endTime)) {
            list.add(startTime);
            startTime = startTime.plusHours(1);
        }

        return list;
    }


}

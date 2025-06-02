package com.beour.reservation.guest.service;

import com.beour.reservation.commons.entity.Reservation;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CheckAvailableTimeService {

    private final AvailableTimeRepository availableTimeRepository;
    private final ReservationRepository reservationRepository;

    public SpaceAvailableTimeResponseDto findAvailableTime(CheckAvailableTimesRequestDto requestDto) {
        AvailableTime availableTime = checkReservationAvailableDateAndGetAvailableTime(requestDto);

        List<Reservation> reservationList = reservationRepository.findBySpaceIdAndDateAndDeletedAtIsNull(
            requestDto.getSpaceId(), requestDto.getDate());

        List<LocalTime> findTimeList = getAvailableTimeList(availableTime, reservationList);
        if(findTimeList.isEmpty()){
            throw new AvailableTimeNotFound("예약 가능한 시간이 없습니다.");
        }

        return SpaceAvailableTimeResponseDto.of(findTimeList);
    }

    public AvailableTime checkReservationAvailableDateAndGetAvailableTime(CheckAvailableTimesRequestDto requestDto) {
        if(requestDto.getDate().isBefore(LocalDate.now())){
            throw new AvailableTimeNotFound("예약 가능한 시간이 없습니다.");
        }

        return availableTimeRepository.findBySpaceIdAndDateAndDeletedAtIsNull(
            requestDto.getSpaceId(), requestDto.getDate()).orElseThrow(
            () -> new AvailableTimeNotFound("예약 가능한 시간이 없습니다.")
        );
    }

    private static List<LocalTime> getAvailableTimeList(AvailableTime availableTime,
        List<Reservation> reservationList) {
        LocalTime startTime = availableTime.getStartTime();
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

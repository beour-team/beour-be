package com.beour.reservation.guest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.contains;

import com.beour.global.exception.error.errorcode.AvailableTimeErrorCode;
import com.beour.global.exception.error.errorcode.ReservationErrorCode;
import com.beour.global.exception.error.errorcode.SpaceErrorCode;
import com.beour.global.jwt.JWTUtil;
import com.beour.reservation.commons.entity.Reservation;
import com.beour.reservation.commons.enums.ReservationStatus;
import com.beour.reservation.commons.enums.UsagePurpose;
import com.beour.reservation.commons.repository.ReservationRepository;
import com.beour.space.domain.entity.AvailableTime;
import com.beour.space.domain.entity.Space;
import com.beour.space.domain.enums.SpaceCategory;
import com.beour.space.domain.enums.UseCategory;
import com.beour.space.domain.repository.AvailableTimeRepository;
import com.beour.space.domain.repository.SpaceRepository;
import com.beour.user.entity.User;
import com.beour.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ReservationGuestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private AvailableTimeRepository availableTimeRepository;
    @Autowired
    private ReservationRepository reservationRepository;


    private User guest;
    private User host;
    private Space space;
    private String accessToken;
    private AvailableTime availableTimePast;
    private AvailableTime availableTimeCurrent;
    private AvailableTime availableTimeNext;

    @BeforeEach
    void setUp() {
        guest = User.builder()
            .loginId("guest")
            .password(passwordEncoder.encode("guestpassword!"))
            .name("게스트")
            .nickname("guest")
            .email("guest@gmail.com")
            .phone("01012345678")
            .role("GUEST")
            .build();
        userRepository.save(guest);

        host = User.builder()
            .loginId("host1")
            .password(passwordEncoder.encode("host1password!"))
            .name("호스트1")
            .nickname("host1")
            .email("host1@gmail.com")
            .phone("01012345678")
            .role("HOST")
            .build();
        userRepository.save(host);

        space = Space.builder()
            .host(host)
            .name("공간1")
            .spaceCategory(SpaceCategory.COOKING)
            .useCategory(UseCategory.COOKING)
            .maxCapacity(3)
            .address("서울시 강남구")
            .detailAddress("투썸건물 2층")
            .pricePerHour(15000)
            .thumbnailUrl("https://example.img")
            .latitude(123.12)
            .longitude(123.12)
            .avgRating(0.0)
            .availableTimes(new ArrayList<>())
            .build();
        spaceRepository.save(space);

        availableTimePast = AvailableTime.builder()
            .space(space)
            .date(LocalDate.now().minusDays(1))
            .startTime(LocalTime.of(1, 0, 0))
            .endTime(LocalTime.of(23, 0, 0))
            .build();
        availableTimeRepository.save(availableTimePast);
        space.getAvailableTimes().add(availableTimePast);

        availableTimeCurrent = AvailableTime.builder()
            .space(space)
            .date(LocalDate.now())
            .startTime(LocalTime.of(1, 0, 0))
            .endTime(LocalTime.of(23, 0, 0))
            .build();
        availableTimeRepository.save(availableTimeCurrent);
        space.getAvailableTimes().add(availableTimeCurrent);

        availableTimeNext = AvailableTime.builder()
            .space(space)
            .date(LocalDate.now().plusDays(1))
            .startTime(LocalTime.of(1, 0, 0))
            .endTime(LocalTime.of(23, 0, 0))
            .build();
        availableTimeRepository.save(availableTimeNext);
        space.getAvailableTimes().add(availableTimeNext);

        accessToken = jwtUtil.createJwt(
            "access",
            guest.getLoginId(),
            "ROLE_" + guest.getRole(),
            1000L * 60 * 30    // 30분
        );
    }

    @AfterEach
    void tearDown() {
        reservationRepository.deleteAll();
        availableTimeRepository.deleteAll();
        spaceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("예약 등록 - 인원 초과")
    void create_reservation_exceed_people_num() throws Exception {
        //given
        String requestJson = String.format("""
            {
                "date": "%s",
                "startTime": "13:00:00",
                "endTime": "14:00:00",
                "price": 15000,
                "guestCount": 4,
                "usagePurpose": "BARISTA_TRAINING",
                "requestMessage": "요청 사항 테스트"
            }
            """, LocalDate.now().plusDays(1));

        //when  then
        mockMvc.perform(post("/api/spaces/" + space.getId() + "/reservations")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(ReservationErrorCode.INVALID_CAPACITY.getMessage()));
    }

    @Test
    @DisplayName("예약 등록 - 시간당 가격과 총 가격이 불일치할 경우")
    void create_reservation_incorrect_price() throws Exception {
        //given
        String requestJson = String.format("""
            {
                "date": "%s",
                "startTime": "13:00:00",
                "endTime": "14:00:00",
                "price": 30000,
                "guestCount": 2,
                "usagePurpose": "BARISTA_TRAINING",
                "requestMessage": "요청 사항 테스트"
            }
            """, LocalDate.now().plusDays(1));

        //when  then
        mockMvc.perform(post("/api/spaces/" + space.getId() + "/reservations")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(ReservationErrorCode.INVALID_PRICE.getMessage()));
    }

    @Test
    @DisplayName("예약 등록 - 예약 날짜가 과거 날짜일 경우")
    void create_reservation_date_past() throws Exception {
        //given
        String requestJson = String.format("""
            {
                "date": "%s",
                "startTime": "13:00:00",
                "endTime": "14:00:00",
                "price": 15000,
                "guestCount": 2,
                "usagePurpose": "BARISTA_TRAINING",
                "requestMessage": "요청 사항 테스트"
            }
            """, LocalDate.now().minusDays(1));

        //when  then
        mockMvc.perform(post("/api/spaces/" + space.getId() + "/reservations")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(AvailableTimeErrorCode.AVAILABLE_TIME_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("예약 등록 - 예약 시간이 과거 시간일 경우")
    void create_reservation_past_time() throws Exception {
        //given
        int currentHour = LocalTime.now().getHour();
        String requestJson = String.format("""
            {
                "date": "%s",
                "startTime": "%s",
                "endTime": "%s",
                "price": 30000,
                "guestCount": 2,
                "usagePurpose": "BARISTA_TRAINING",
                "requestMessage": "요청 사항 테스트"
            }
            """, LocalDate.now(), LocalTime.of(currentHour - 1, 0, 0), LocalTime.of(currentHour + 1, 0, 0));

        //when  then
        mockMvc.perform(post("/api/spaces/" + space.getId() + "/reservations")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(AvailableTimeErrorCode.AVAILABLE_TIME_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("예약 등록 - 예약 날짜 및 시간이 다른 예약과 겹칠 경우")
    void create_reservation_conflict_other_reservation() throws Exception {
        //given
        Reservation reservationPast = Reservation.builder()
            .guest(guest)
            .host(host)
            .space(space)
            .status(ReservationStatus.COMPLETED)
            .usagePurpose(UsagePurpose.BARISTA_TRAINING)
            .requestMessage("테슽뚜")
            .date(LocalDate.now().plusDays(1))
            .startTime(LocalTime.of(12, 0, 0))
            .endTime(LocalTime.of(16, 0, 0))
            .price(60000)
            .guestCount(2)
            .build();
        reservationRepository.save(reservationPast);

        String requestJson = String.format("""
            {
                "date": "%s",
                "startTime": "15:00:00",
                "endTime": "17:00:00",
                "price": 30000,
                "guestCount": 2,
                "usagePurpose": "BARISTA_TRAINING",
                "requestMessage": "요청 사항 테스트"
            }
            """, LocalDate.now().plusDays(1));

        //when  then
        mockMvc.perform(post("/api/spaces/" + space.getId() + "/reservations")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(AvailableTimeErrorCode.TIME_UNAVAILABLE.getMessage()));
    }

    @Test
    @DisplayName("예약 등록 - 존재하지 않는 공간")
    void create_reservation_not_fount_space() throws Exception {
        //given
        String requestJson = String.format("""
            {
                "date": "%s",
                "startTime": "15:00:00",
                "endTime": "17:00:00",
                "price": 30000,
                "guestCount": 2,
                "usagePurpose": "BARISTA_TRAINING",
                "requestMessage": "요청 사항 테스트"
            }
            """, LocalDate.now().plusDays(1));

        //when  then
        mockMvc.perform(post("/api/spaces/" + 100 + "/reservations")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(SpaceErrorCode.SPACE_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("예약 등록 - 성공")
    void success_create_reservation() throws Exception {
        //given
        String requestJson = String.format("""
            {
                "date": "%s",
                "startTime": "15:00:00",
                "endTime": "17:00:00",
                "price": 30000,
                "guestCount": 2,
                "usagePurpose": "BARISTA_TRAINING",
                "requestMessage": "요청 사항 테스트"
            }
            """, LocalDate.now().plusDays(1));

        //when  then
        mockMvc.perform(post("/api/spaces/" + space.getId() + "/reservations")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
            )
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("이용 가능한 시간 조회 - 과거 날짜로 조회")
    void check_available_time_with_past_date() throws Exception {
        //when  then
        mockMvc.perform(get("/api/spaces/"+ space.getId() +"/available-times/date?date=" + LocalDate.now().minusDays(1))
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(AvailableTimeErrorCode.AVAILABLE_TIME_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("이용 가능한 시간 조회 - 오늘 날짜로 조회")
    void check_available_time_with_today() throws Exception {
        List<String> availableTimes = new ArrayList<>();
        int currentHour = LocalTime.now().getHour();
        for(int i = currentHour + 1; i <= 22; i++){
            availableTimes.add(String.format("%02d:00:00", i));
        }

        //when  then
        mockMvc.perform(get("/api/spaces/"+ space.getId() +"/available-times/date?date=" + LocalDate.now())
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.timeList.length()").value(availableTimes.size()))
            .andExpect(jsonPath("$.data.timeList").value(contains(availableTimes.toArray())));
    }

    @Test
    @DisplayName("이용 가능한 시간 조회 - 가능한 시간 없을 경우")
    void check_available_time_not_found() throws Exception {
        //when  then
        mockMvc.perform(get("/api/spaces/"+ space.getId() +"/available-times/date?date=" + LocalDate.now().plusDays(3))
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(AvailableTimeErrorCode.AVAILABLE_TIME_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("이용 가능한 시간 조회 - 해당 날에 예약이 있을 경우")
    void check_available_time_has_other_reservation() throws Exception {
        //given
        Reservation reservationPast = Reservation.builder()
            .guest(guest)
            .host(host)
            .space(space)
            .status(ReservationStatus.COMPLETED)
            .usagePurpose(UsagePurpose.BARISTA_TRAINING)
            .requestMessage("테슽뚜")
            .date(LocalDate.now().plusDays(1))
            .startTime(LocalTime.of(1, 0, 0))
            .endTime(LocalTime.of(5, 0, 0))
            .price(60000)
            .guestCount(2)
            .build();
        reservationRepository.save(reservationPast);

        List<String> availableTimes = new ArrayList<>();
        for(int i = 5; i <= 22; i++){
            availableTimes.add(String.format("%02d:00:00", i));
        }

        //when  then
        mockMvc.perform(get("/api/spaces/"+ space.getId() +"/available-times/date?date=" + LocalDate.now().plusDays(1))
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.timeList.length()").value(availableTimes.size()))
            .andExpect(jsonPath("$.data.timeList").value(contains(availableTimes.toArray())));
    }

    @Test
    @Transactional
    @DisplayName("이용 가능한 시간 조회 - 예약 거절된 시간 조회")
    void check_available_time_has_other_rejected_reservation() throws Exception {
        //given
        Reservation reservation = Reservation.builder()
            .guest(guest)
            .host(host)
            .space(space)
            .status(ReservationStatus.COMPLETED)
            .usagePurpose(UsagePurpose.BARISTA_TRAINING)
            .requestMessage("테슽뚜")
            .date(LocalDate.now().plusDays(1))
            .startTime(LocalTime.of(1, 0, 0))
            .endTime(LocalTime.of(5, 0, 0))
            .price(60000)
            .guestCount(2)
            .build();
        reservationRepository.save(reservation);
        reservation.updateStatus(ReservationStatus.REJECTED);

        List<String> availableTimes = new ArrayList<>();
        for(int i = 1; i <= 22; i++){
            availableTimes.add(String.format("%02d:00:00", i));
        }

        //when  then
        mockMvc.perform(get("/api/spaces/"+ space.getId() +"/available-times/date?date=" + LocalDate.now().plusDays(1))
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.timeList.length()").value(availableTimes.size()))
            .andExpect(jsonPath("$.data.timeList").value(contains(availableTimes.toArray())));
    }

    @Test
    @DisplayName("예약 현황 조회 - 현 시점의 시간 이전의 시간 잘 걸러지는지")
    void check_reservation_list_filtering_past_reservation() throws Exception {
        //given
        int currentHour = LocalTime.now().getHour();
        Reservation reservationPast = Reservation.builder()
            .guest(guest)
            .host(host)
            .space(space)
            .status(ReservationStatus.COMPLETED)
            .usagePurpose(UsagePurpose.BARISTA_TRAINING)
            .requestMessage("테슽뚜")
            .date(LocalDate.now())
            .startTime(LocalTime.of(currentHour -1, 0, 0))
            .endTime(LocalTime.of(currentHour, 0, 0))
            .price(15000)
            .guestCount(2)
            .build();
        reservationRepository.save(reservationPast);
        Reservation reservationFuture = Reservation.builder()
            .guest(guest)
            .host(host)
            .space(space)
            .status(ReservationStatus.ACCEPTED)
            .usagePurpose(UsagePurpose.BARISTA_TRAINING)
            .requestMessage("테슽뚜")
            .date(LocalDate.now())
            .startTime(LocalTime.of(currentHour + 1, 0, 0))
            .endTime(LocalTime.of(currentHour + 2, 0, 0))
            .price(15000)
            .guestCount(2)
            .build();
        reservationRepository.save(reservationFuture);

        //when  then
        mockMvc.perform(get("/api/reservations/current")
                .header("Authorization", "Bearer " + accessToken)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.reservations[0].spaceName").value(reservationFuture.getSpace().getName()))
            .andExpect(jsonPath("$.data.reservations[0].startTime").value(String.format("%02d:00:00", reservationFuture.getStartTime().getHour())))
            .andExpect(jsonPath("$.data.reservations[0].endTime").value(String.format("%02d:00:00", reservationFuture.getEndTime().getHour())));
    }

    @Test
    @DisplayName("예약 현황 조회 - 진행 중인 예약 있을 경우")
    void check_reservation_list_with_ing_reservation() throws Exception {
        //given
        int currentHour = LocalTime.now().getHour();
        Reservation reservation = Reservation.builder()
            .guest(guest)
            .host(host)
            .space(space)
            .status(ReservationStatus.COMPLETED)
            .usagePurpose(UsagePurpose.BARISTA_TRAINING)
            .requestMessage("테슽뚜")
            .date(LocalDate.now())
            .startTime(LocalTime.of(currentHour - 1, 0, 0))
            .endTime(LocalTime.of(currentHour + 1, 0, 0))
            .price(15000)
            .guestCount(2)
            .build();
        reservationRepository.save(reservation);

        //when  then
        mockMvc.perform(get("/api/reservations/current")
                .header("Authorization", "Bearer " + accessToken)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.reservations[0].spaceName").value(reservation.getSpace().getName()))
            .andExpect(jsonPath("$.data.reservations[0].startTime").value(String.format("%02d:00:00", reservation.getStartTime().getHour())))
            .andExpect(jsonPath("$.data.reservations[0].endTime").value(String.format("%02d:00:00", reservation.getEndTime().getHour())))
            .andExpect(jsonPath("$.data.reservations[0].currentUsing").value(true));
    }

    @Test
    @DisplayName("예약 현황 조회 - 예약 없을 경우")
    void check_reservation_list_not_found() throws Exception {
        //when  then
        mockMvc.perform(get("/api/reservations/current")
                .header("Authorization", "Bearer " + accessToken)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ReservationErrorCode.RESERVATION_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("지난 예약 현황 조회 - 예약 없을 경우")
    void check_past_reservation_list_not_found() throws Exception {
        //when  then
        mockMvc.perform(get("/api/reservations/past")
                .header("Authorization", "Bearer " + accessToken)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ReservationErrorCode.RESERVATION_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("지난 예약 현황 조회 - 시간 지나면 예약 사용 완료 상태로 변경")
    void check_past_reservation_list_use_complete() throws Exception {
        //given
        Reservation reservationPast = Reservation.builder()
            .guest(guest)
            .host(host)
            .space(space)
            .status(ReservationStatus.ACCEPTED)
            .usagePurpose(UsagePurpose.BARISTA_TRAINING)
            .requestMessage("테슽뚜")
            .date(LocalDate.now().minusDays(1))
            .startTime(LocalTime.of(13, 0, 0))
            .endTime(LocalTime.of(14, 0, 0))
            .price(15000)
            .guestCount(2)
            .build();
        reservationRepository.save(reservationPast);

        //when  then
        mockMvc.perform(get("/api/reservations/past")
                .header("Authorization", "Bearer " + accessToken)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.reservations[0].status").value(ReservationStatus.COMPLETED.getText()));
    }

    @Test
    @DisplayName("예약 취소 - 존재하지 않는 에약일 경우")
    void cancel_reservation_not_found() throws Exception {
        //when  then
        mockMvc.perform(delete("/api/reservations/100")
                .header("Authorization", "Bearer " + accessToken)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ReservationErrorCode.RESERVATION_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("예약 취소 - 이미 확정된 예약일 경우")
    void cancel_reservation_status_accepted() throws Exception {
        //given
        Reservation reservation = Reservation.builder()
            .guest(guest)
            .host(host)
            .space(space)
            .status(ReservationStatus.ACCEPTED)
            .usagePurpose(UsagePurpose.BARISTA_TRAINING)
            .requestMessage("테슽뚜")
            .date(LocalDate.now().plusDays(1))
            .startTime(LocalTime.of(13, 0, 0))
            .endTime(LocalTime.of(14, 0, 0))
            .price(15000)
            .guestCount(2)
            .build();
        reservationRepository.save(reservation);

        //when  then
        mockMvc.perform(delete("/api/reservations/" + reservation.getId())
                .header("Authorization", "Bearer " + accessToken)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(ReservationErrorCode.CANNOT_CANCEL_RESERVATION.getMessage()));
    }

    @Test
    @Transactional
    @DisplayName("예약 취소 - 성공")
    void success_cancel_reservation() throws Exception {
        //given
        Reservation reservation = Reservation.builder()
            .guest(guest)
            .host(host)
            .space(space)
            .status(ReservationStatus.PENDING)
            .usagePurpose(UsagePurpose.BARISTA_TRAINING)
            .requestMessage("테슽뚜")
            .date(LocalDate.now().plusDays(1))
            .startTime(LocalTime.of(13, 0, 0))
            .endTime(LocalTime.of(14, 0, 0))
            .price(15000)
            .guestCount(2)
            .build();
        reservationRepository.save(reservation);

        //when  then
        mockMvc.perform(delete("/api/reservations/" + reservation.getId())
                .header("Authorization", "Bearer " + accessToken)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").value("예약이 성공적으로 취소되었습니다."));

        assertEquals(ReservationStatus.REJECTED, reservation.getStatus());
    }

    @Test
    @Transactional
    @DisplayName("취소된 예약 조회 - 성공")
    void success_get_canceled_reservations() throws Exception {
        //given
        Reservation reservation = Reservation.builder()
            .guest(guest)
            .host(host)
            .space(space)
            .status(ReservationStatus.REJECTED)
            .usagePurpose(UsagePurpose.BARISTA_TRAINING)
            .requestMessage("테슽뚜")
            .date(LocalDate.now().plusDays(1))
            .startTime(LocalTime.of(13, 0, 0))
            .endTime(LocalTime.of(14, 0, 0))
            .price(15000)
            .guestCount(2)
            .build();
        reservationRepository.save(reservation);

        //when  then
        mockMvc.perform(get("/api/reservations/status")
                .header("Authorization", "Bearer " + accessToken)
                .param("status", ReservationStatus.REJECTED.name())
                .param("page", "0")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.reservations[0].reservationId").value(reservation.getId()))
            .andExpect(jsonPath("$.data.reservations[0].spaceName").value(space.getName()))
            .andExpect(jsonPath("$.data.reservations[0].status").value(ReservationStatus.REJECTED.getText()))
        ;
    }

    @Test
    @Transactional
    @DisplayName("취소된 예약 조회 - 취소한 예약 없을 경우")
    void get_canceled_reservations_reservations_not_found() throws Exception {
        //when  then
        mockMvc.perform(get("/api/reservations/status")
                .header("Authorization", "Bearer " + accessToken)
                .param("status", ReservationStatus.REJECTED.name())
                .param("page", "0")
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ReservationErrorCode.RESERVATION_NOT_FOUND.getMessage()))
        ;
    }

    @Test
    @DisplayName("예약 상세 조회 - 성공")
    void success_get_detail_reservation_information() throws Exception {
        //given
        Reservation reservation = Reservation.builder()
            .guest(guest)
            .host(host)
            .space(space)
            .status(ReservationStatus.COMPLETED)
            .usagePurpose(UsagePurpose.BARISTA_TRAINING)
            .requestMessage("테슽뚜")
            .date(LocalDate.now().plusDays(1))
            .startTime(LocalTime.of(12, 0, 0))
            .endTime(LocalTime.of(16, 0, 0))
            .price(60000)
            .guestCount(2)
            .build();
        reservationRepository.save(reservation);

        //when  then
        mockMvc.perform(get("/api/reservations/" + reservation.getId())
                .header("Authorization", "Bearer " + accessToken)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.reservationId").value(reservation.getId()));
    }

    @Test
    @DisplayName("예약 상세 조회 - 해당 예약이 없음")
    void get_detail_reservation_information_reservation_not_found() throws Exception {
        //when  then
        mockMvc.perform(get("/api/reservations/1")
                .header("Authorization", "Bearer " + accessToken)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ReservationErrorCode.RESERVATION_NOT_FOUND.getMessage()));
    }
}
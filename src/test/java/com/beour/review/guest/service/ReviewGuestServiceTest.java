package com.beour.review.guest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.beour.global.exception.exceptionType.DuplicateException;
import com.beour.global.exception.exceptionType.ReviewNotFoundException;
import com.beour.global.exception.exceptionType.UnauthorityException;
import com.beour.global.file.ImageUploader;
import com.beour.reservation.commons.entity.Reservation;
import com.beour.reservation.commons.enums.ReservationStatus;
import com.beour.reservation.commons.enums.UsagePurpose;
import com.beour.global.exception.exceptionType.MissMatch;
import com.beour.global.exception.exceptionType.ReservationNotFound;
import com.beour.reservation.commons.repository.ReservationRepository;
import com.beour.review.domain.entity.Review;
import com.beour.review.domain.entity.ReviewImage;
import com.beour.review.domain.repository.ReviewCommentRepository;
import com.beour.review.domain.repository.ReviewImageRepository;
import com.beour.review.domain.repository.ReviewRepository;
import com.beour.review.guest.dto.RecentWrittenReviewResponseDto;
import com.beour.review.guest.dto.ReviewDetailResponseDto;
import com.beour.review.guest.dto.ReviewForReservationResponseDto;
import com.beour.review.guest.dto.ReviewRequestDto;
import com.beour.review.guest.dto.ReviewUpdateRequestDto;
import com.beour.review.guest.dto.ReviewableReservationPageResponseDto;
import com.beour.review.guest.dto.WrittenReviewPageResponseDto;
import com.beour.space.domain.entity.Space;
import com.beour.space.domain.repository.SpaceRepository;
import com.beour.space.domain.enums.SpaceCategory;
import com.beour.space.domain.enums.UseCategory;
import com.beour.user.entity.User;
import com.beour.user.repository.UserRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@ActiveProfiles("test")
@SpringBootTest
class ReviewGuestServiceTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ImageUploader imageUploader() {
            return Mockito.mock(ImageUploader.class);
        }
    }

    @Autowired
    private ReviewGuestService reviewGuestService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewImageRepository reviewImageRepository;
    @Autowired
    private ReviewCommentRepository reviewCommentRepository;
    @Autowired
    private ImageUploader imageUploader;

    private User guest;
    private User host;
    private Space space;
    private Reservation completedReservation;
    private Reservation acceptedReservation;
    private Pageable pageable;

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

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                guest.getLoginId(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

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

        completedReservation = Reservation.builder()
                .guest(guest)
                .host(host)
                .space(space)
                .status(ReservationStatus.COMPLETED)
                .usagePurpose(UsagePurpose.BARISTA_TRAINING)
                .requestMessage("테슽뚜")
                .date(LocalDate.now().minusDays(1))
                .startTime(LocalTime.of(12, 0, 0))
                .endTime(LocalTime.of(14, 0, 0))
                .price(30000)
                .guestCount(2)
                .build();
        reservationRepository.save(completedReservation);

        acceptedReservation = Reservation.builder()
                .guest(guest)
                .host(host)
                .space(space)
                .status(ReservationStatus.ACCEPTED)
                .usagePurpose(UsagePurpose.BARISTA_TRAINING)
                .requestMessage("테슽뚜")
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(15, 0, 0))
                .endTime(LocalTime.of(16, 0, 0))
                .price(15000)
                .guestCount(2)
                .build();
        reservationRepository.save(acceptedReservation);

        pageable = PageRequest.of(0, 10);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        reviewImageRepository.deleteAll();
        reviewCommentRepository.deleteAll();
        reviewRepository.deleteAll();
        reservationRepository.deleteAll();
        spaceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("리뷰 작성 가능한 예약 조회 - 완료된 예약만 조회")
    void get_reviewable_reservations_only_completed() {
        //when
        ReviewableReservationPageResponseDto result = reviewGuestService.getReviewableReservations(pageable);

        //then
        assertThat(result.getReservations()).hasSize(1);
        assertEquals(completedReservation.getId(), result.getReservations().get(0).getReservationId());
        assertEquals(completedReservation.getSpace().getName(), result.getReservations().get(0).getSpaceName());
        assertEquals(completedReservation.getDate(), result.getReservations().get(0).getDate());
        assertEquals(completedReservation.getStartTime(), result.getReservations().get(0).getStartTime());
        assertEquals(completedReservation.getEndTime(), result.getReservations().get(0).getEndTime());
        assertEquals(completedReservation.getGuestCount(), result.getReservations().get(0).getGuestCount());
        assertEquals(completedReservation.getUsagePurpose().getText(), result.getReservations().get(0).getUsagePurpose());
    }

    @Test
    @DisplayName("리뷰 작성 가능한 예약 조회 - 빈 페이지")
    void get_reviewable_reservations_empty_page() {
        //given
        // 완료된 예약 삭제
        reservationRepository.delete(completedReservation);

        // when
        ReviewableReservationPageResponseDto result = reviewGuestService.getReviewableReservations(pageable);

        // then
        assertNotNull(result);
        assertTrue(result.getReservations().isEmpty(), "리뷰 가능한 예약이 없으면 빈 리스트를 반환");
        assertEquals(0, result.getTotalPage());
        assertTrue(result.isLast());
    }

    @Test
    @DisplayName("작성한 리뷰 조회 - 빈 페이지")
    void get_written_reviews_empty_page() {
        //when then
        assertThrows(ReviewNotFoundException.class,
                () -> reviewGuestService.getWrittenReviews(pageable));
    }

    @Test
    @DisplayName("작성한 리뷰 조회 - 리뷰 존재")
    void get_written_reviews_with_reviews() {
        //given
        Review review = Review.builder()
                .guest(guest)
                .space(space)
                .reservation(completedReservation)
                .rating(5)
                .content("좋은 공간이었습니다.")
                .reservedDate(completedReservation.getDate())
                .build();
        reviewRepository.save(review);

        //when
        WrittenReviewPageResponseDto result = reviewGuestService.getWrittenReviews(pageable);

        //then
        assertThat(result.getReviews()).hasSize(1);
        assertEquals(review.getId(), result.getReviews().get(0).getReviewId());
        assertEquals(guest.getNickname(), result.getReviews().get(0).getGuestNickname());
        assertEquals(5, result.getReviews().get(0).getReviewRating());
        assertEquals(space.getName(), result.getReviews().get(0).getSpaceName());
        assertEquals(completedReservation.getDate(), result.getReviews().get(0).getReservationDate());
        assertEquals("좋은 공간이었습니다.", result.getReviews().get(0).getReviewContent());
    }

    @Test
    @DisplayName("리뷰 작성을 위한 예약 정보 조회 - 존재하지 않는 예약")
    void get_reservation_for_review_not_found() {
        //when then
        assertThrows(ReservationNotFound.class,
                () -> reviewGuestService.getReservationForReview(999L));
    }

    @Test
    @DisplayName("리뷰 작성을 위한 예약 정보 조회 - 권한 없음")
    void get_reservation_for_review_unauthorized() {
        //given
        User otherGuest = User.builder()
                .loginId("otherguest")
                .password(passwordEncoder.encode("password!"))
                .name("다른게스트")
                .nickname("otherguest")
                .email("other@gmail.com")
                .phone("01012345679")
                .role("GUEST")
                .build();
        userRepository.save(otherGuest);

        Reservation otherReservation = Reservation.builder()
                .guest(otherGuest)
                .host(host)
                .space(space)
                .status(ReservationStatus.COMPLETED)
                .usagePurpose(UsagePurpose.BARISTA_TRAINING)
                .requestMessage("테슽뚜")
                .date(LocalDate.now().minusDays(1))
                .startTime(LocalTime.of(10, 0, 0))
                .endTime(LocalTime.of(12, 0, 0))
                .price(30000)
                .guestCount(2)
                .build();
        reservationRepository.save(otherReservation);

        //when then
        assertThrows(UnauthorityException.class,
                () -> reviewGuestService.getReservationForReview(otherReservation.getId()));
    }

    @Test
    @Transactional
    @DisplayName("리뷰 작성을 위한 예약 정보 조회 - 성공")
    void get_reservation_for_review_success() {
        //when
        ReviewForReservationResponseDto result = reviewGuestService.getReservationForReview(completedReservation.getId());

        //then
        assertEquals(completedReservation.getId(), result.getReservationId());
        assertEquals(completedReservation.getSpace().getName(), result.getSpaceName());
        assertEquals(completedReservation.getDate(), result.getDate());
        assertEquals(completedReservation.getStartTime(), result.getStartTime());
        assertEquals(completedReservation.getEndTime(), result.getEndTime());
        assertEquals(completedReservation.getGuestCount(), result.getGuestCount());
        assertEquals(completedReservation.getUsagePurpose().getText(), result.getUsagePurpose());
    }

    @Test
    @DisplayName("리뷰 작성 - 완료되지 않은 예약")
    void create_review_not_completed_reservation() throws IOException {
        //given
        ReviewRequestDto requestDto = new ReviewRequestDto(
                acceptedReservation.getId(), 5, "좋은 공간이었습니다.");
        List<MultipartFile> images = Collections.emptyList();

        //when then
        assertThrows(MissMatch.class,
                () -> reviewGuestService.createReview(requestDto, images));
    }

    @Test
    @DisplayName("리뷰 작성 - 중복 리뷰")
    void create_review_duplicate() throws IOException {
        //given
        Review existingReview = Review.builder()
                .guest(guest)
                .space(space)
                .reservation(completedReservation)
                .rating(4)
                .content("이미 작성한 리뷰")
                .reservedDate(completedReservation.getDate())
                .build();
        reviewRepository.save(existingReview);

        ReviewRequestDto requestDto = new ReviewRequestDto(
                completedReservation.getId(), 5, "좋은 공간이었습니다.");
        List<MultipartFile> images = Collections.emptyList();

        //when then
        assertThrows(DuplicateException.class,
                () -> reviewGuestService.createReview(requestDto, images));
    }

    @Test
    @Transactional
    @DisplayName("리뷰 작성 - 성공 (이미지 없음)")
    void create_review_success_without_images() throws IOException {
        //given
        ReviewRequestDto requestDto = new ReviewRequestDto(
                completedReservation.getId(), 5, "좋은 공간이었습니다.");
        List<MultipartFile> images = Collections.emptyList();

        //when
        reviewGuestService.createReview(requestDto, images);

        //then
        List<Review> reviews = reviewRepository.findAll();
        assertThat(reviews).hasSize(1);
        Review savedReview = reviews.get(0);
        assertEquals(guest.getId(), savedReview.getGuest().getId());
        assertEquals(space.getId(), savedReview.getSpace().getId());
        assertEquals(5, savedReview.getRating());
        assertEquals("좋은 공간이었습니다.", savedReview.getContent());
        assertEquals(completedReservation.getDate(), savedReview.getReservedDate());
        assertThat(savedReview.getImages() == null || savedReview.getImages().isEmpty()).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("리뷰 작성 - 성공 (이미지 포함)")
    void create_review_success_with_images() throws IOException {
        //given
        ReviewRequestDto requestDto = new ReviewRequestDto(
                completedReservation.getId(), 5, "좋은 공간이었습니다.");

        MockMultipartFile image1 = new MockMultipartFile("image1", "image1.jpg", "image/jpeg", "image1 content".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("image2", "image2.jpg", "image/jpeg", "image2 content".getBytes());
        List<MultipartFile> images = List.of(image1, image2);

        when(imageUploader.upload(any(MultipartFile.class)))
                .thenReturn("https://example.com/image1.jpg")
                .thenReturn("https://example.com/image2.jpg");

        //when
        reviewGuestService.createReview(requestDto, images);

        //then
        List<Review> reviews = reviewRepository.findAll();
        assertThat(reviews).hasSize(1);
        Review savedReview = reviews.get(0);
        assertEquals(guest.getId(), savedReview.getGuest().getId());
        assertEquals(space.getId(), savedReview.getSpace().getId());
        assertEquals(5, savedReview.getRating());
        assertEquals("좋은 공간이었습니다.", savedReview.getContent());
        assertEquals(completedReservation.getDate(), savedReview.getReservedDate());
        assertEquals(2, savedReview.getImages().size());
    }

    @Test
    @DisplayName("리뷰 상세 조회 - 존재하지 않는 리뷰")
    void get_review_detail_not_found() {
        //when then
        assertThrows(ReviewNotFoundException.class,
                () -> reviewGuestService.getReviewDetail(999L));
    }

    @Test
    @DisplayName("리뷰 상세 조회 - 권한 없음")
    void get_review_detail_unauthorized() {
        //given
        User otherGuest = User.builder()
                .loginId("otherguest")
                .password(passwordEncoder.encode("password!"))
                .name("다른게스트")
                .nickname("otherguest")
                .email("other@gmail.com")
                .phone("01012345679")
                .role("GUEST")
                .build();
        userRepository.save(otherGuest);

        Review otherReview = Review.builder()
                .guest(otherGuest)
                .space(space)
                .reservation(completedReservation)
                .rating(4)
                .content("다른 사람의 리뷰")
                .reservedDate(LocalDate.now().minusDays(1))
                .build();
        reviewRepository.save(otherReview);

        //when then
        assertThrows(UnauthorityException.class,
                () -> reviewGuestService.getReviewDetail(otherReview.getId()));
    }

    @Test
    @Transactional
    @DisplayName("리뷰 상세 조회 - 성공")
    void get_review_detail_success() {
        //given
        ReviewImage image1 = ReviewImage.builder()
                .imageUrl("https://example.com/image1.jpg")
                .build();
        ReviewImage image2 = ReviewImage.builder()
                .imageUrl("https://example.com/image2.jpg")
                .build();

        Review review = Review.builder()
                .guest(guest)
                .space(space)
                .reservation(completedReservation)
                .rating(5)
                .content("좋은 공간이었습니다.")
                .reservedDate(completedReservation.getDate())
                .build();

        review.addImage(image1);
        review.addImage(image2);
        reviewRepository.save(review);

        //when
        ReviewDetailResponseDto result = reviewGuestService.getReviewDetail(review.getId());

        //then
        assertEquals(review.getId(), result.getReviewId());
        assertEquals(5, result.getRating());
        assertEquals("좋은 공간이었습니다.", result.getContent());
        assertEquals(completedReservation.getDate(), result.getReservedDate());
        assertEquals(2, result.getImageUrls().size());
    }

    @Test
    @Transactional
    @DisplayName("리뷰 수정 - 성공 (이미지 없음)")
    void update_review_success_without_images() throws IOException {
        //given
        Review review = Review.builder()
                .guest(guest)
                .space(space)
                .reservation(completedReservation)
                .rating(4)
                .content("원래 리뷰")
                .reservedDate(completedReservation.getDate())
                .build();
        reviewRepository.save(review);

        ReviewUpdateRequestDto requestDto = new ReviewUpdateRequestDto(5, "수정된 리뷰");
        List<MultipartFile> images = Collections.emptyList();

        //when
        reviewGuestService.updateReview(review.getId(), requestDto, images);

        //then
        Review updatedReview = reviewRepository.findById(review.getId()).orElseThrow();
        assertEquals(5, updatedReview.getRating());
        assertEquals("수정된 리뷰", updatedReview.getContent());
        assertThat(updatedReview.getImages() == null || updatedReview.getImages().isEmpty()).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("리뷰 수정 - 성공 (이미지 포함)")
    void update_review_success_with_images() throws IOException {
        //given
        ReviewImage existingImage = ReviewImage.builder()
                .imageUrl("https://example.com/old-image.jpg")
                .build();

        Review review = Review.builder()
                .guest(guest)
                .space(space)
                .reservation(completedReservation)
                .rating(4)
                .content("원래 리뷰")
                .reservedDate(completedReservation.getDate())
                .build();

        review.addImage(existingImage);
        reviewRepository.save(review);

        MockMultipartFile newImage = new MockMultipartFile("newImage", "new-image.jpg", "image/jpeg", "new image content".getBytes());
        List<MultipartFile> images = List.of(newImage);
        ReviewUpdateRequestDto requestDto = new ReviewUpdateRequestDto(5, "수정된 리뷰");

        when(imageUploader.upload(any(MultipartFile.class)))
                .thenReturn("https://example.com/new-image.jpg");

        //when
        reviewGuestService.updateReview(review.getId(), requestDto, images);

        //then
        Review updatedReview = reviewRepository.findById(review.getId()).orElseThrow();
        assertEquals(5, updatedReview.getRating());
        assertEquals("수정된 리뷰", updatedReview.getContent());
        assertEquals(1, updatedReview.getImages().size());
        assertEquals("https://example.com/new-image.jpg", updatedReview.getImages().get(0).getImageUrl());
    }

    @Test
    @Transactional
    @DisplayName("리뷰 삭제 - 성공")
    void delete_review_success() {
        //given
        Review review = Review.builder()
                .guest(guest)
                .space(space)
                .reservation(completedReservation)
                .rating(5)
                .content("삭제될 리뷰")
                .reservedDate(completedReservation.getDate())
                .build();
        reviewRepository.save(review);

        //when
        reviewGuestService.deleteReview(review.getId());

        //then
        Review deletedReview = reviewRepository.findById(review.getId()).orElseThrow();
        assertNotNull(deletedReview.getDeletedAt());
    }

    @Test
    @DisplayName("최근 작성된 리뷰 조회 - 리뷰 없음")
    void get_recent_written_reviews_empty() {
        //when then
        assertThrows(ReviewNotFoundException.class,
                () -> reviewGuestService.getRecentWrittenReviews());
    }

    @Test
    @Transactional
    @DisplayName("최근 작성된 리뷰 조회 - 성공")
    void get_recent_written_reviews_success() {
        //given
        Reservation completedReservation2 = Reservation.builder()
                .guest(guest)
                .host(host)
                .space(space)
                .status(ReservationStatus.COMPLETED)
                .usagePurpose(UsagePurpose.BARISTA_TRAINING)
                .requestMessage("테슽뚜")
                .date(LocalDate.now().minusDays(1))
                .startTime(LocalTime.of(14, 0, 0))
                .endTime(LocalTime.of(16, 0, 0))
                .price(30000)
                .guestCount(2)
                .build();
        reservationRepository.save(completedReservation2);

        Review review1 = Review.builder()
                .guest(guest)
                .space(space)
                .reservation(completedReservation)
                .rating(5)
                .content("첫 번째 리뷰")
                .reservedDate(completedReservation.getDate())
                .build();
        reviewRepository.save(review1);
        reviewRepository.flush();

        ReviewImage image = ReviewImage.builder()
                .imageUrl("https://example.com/image.jpg")
                .build();

        Review review2 = Review.builder()
                .guest(guest)
                .space(space)
                .reservation(completedReservation2)
                .rating(4)
                .content("두 번째 리뷰")
                .reservedDate(completedReservation2.getDate())
                .build();

        review2.addImage(image);
        reviewRepository.save(review2);

        //when
        List<RecentWrittenReviewResponseDto> result = reviewGuestService.getRecentWrittenReviews();

        //then
        assertThat(result).hasSize(2);
        assertEquals(space.getName(), result.get(0).getSpaceName());
        assertEquals(guest.getNickname(), result.get(0).getReviewerNickName());
        assertEquals(4, result.get(0).getRating());
        assertEquals("두 번째 리뷰", result.get(0).getReviewContent());
    }
}

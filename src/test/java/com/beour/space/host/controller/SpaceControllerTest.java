package com.beour.space.host.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import com.beour.global.exception.error.errorcode.SpaceErrorCode;
import com.beour.space.domain.entity.Description;
import com.beour.space.domain.entity.Space;
import com.beour.space.domain.entity.Tag;
import com.beour.space.domain.enums.SpaceCategory;
import com.beour.space.domain.enums.UseCategory;
import com.beour.space.domain.repository.DescriptionRepository;
import com.beour.space.domain.repository.SpaceRepository;
import com.beour.space.domain.repository.TagRepository;
import com.beour.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.beour.user.entity.User;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class SpaceControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private DescriptionRepository descriptionRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private User host1;
    private User host2;
    private Space space1;
    private Description description1;
    private Tag tag1;
    private Space space2;
    private Description description2;

    @BeforeEach
    void setUp() {
        host1 = User.builder()
                .loginId("host1")
                .password(passwordEncoder.encode("host1password!"))
                .name("호스트1")
                .nickname("host1")
                .email("host1@gmail.com")
                .phone("01012345678")
                .role("HOST")
                .build();
        userRepository.save(host1);

        host2 = User.builder()
                .loginId("host2")
                .password(passwordEncoder.encode("host2password!"))
                .name("호스트2")
                .nickname("host2")
                .email("host2@gmail.com")
                .phone("01087654321")
                .role("HOST")
                .build();
        userRepository.save(host2);

        space1 = Space.builder()
                .host(host1)
                .name("테스트 공간1")
                .spaceCategory(SpaceCategory.COOKING)
                .useCategory(UseCategory.COOKING)
                .maxCapacity(10)
                .address("서울특별시 강남구 역삼동")
                .detailAddress("테스트빌딩 1층")
                .pricePerHour(15000)
                .thumbnailUrl("https://example.com/thumbnail1.jpg")
                .latitude(37.5665)
                .longitude(126.9780)
                .avgRating(4.5)
                .availableTimes(new ArrayList<>())
                .build();
        spaceRepository.save(space1);

        description1 = Description.builder()
                .space(space1)
                .description("테스트 공간 설명")
                .priceGuide("시간당 15,000원")
                .facilityNotice("주방 시설 완비")
                .notice("사전 예약 필수")
                .locationDescription("지하철역 도보 5분")
                .refundPolicy("24시간 전 취소 가능")
                .build();
        descriptionRepository.save(description1);

        tag1 = Tag.builder()
                .space(space1)
                .contents("요리")
                .build();
        tagRepository.save(tag1);

        space2 = Space.builder()
                .host(host2)
                .name("테스트 공간2")
                .spaceCategory(SpaceCategory.CAFE)
                .useCategory(UseCategory.MEETING)
                .maxCapacity(20)
                .address("서울특별시 강남구 테헤란로 212")
                .detailAddress("테스트빌딩 2층")
                .pricePerHour(20000)
                .thumbnailUrl("https://example.com/thumbnail2.jpg")
                .latitude(37.4833)
                .longitude(127.0322)
                .avgRating(4.0)
                .availableTimes(new ArrayList<>())
                .build();
        spaceRepository.save(space2);

        description2 = Description.builder()
                .space(space2)
                .description("카페형 회의 공간")
                .priceGuide("시간당 20,000원")
                .facilityNotice("프로젝터, 화이트보드 제공")
                .notice("최대 20명까지 수용 가능")
                .locationDescription("강남역 도보 10분")
                .refundPolicy("12시간 전 취소 가능")
                .build();
        descriptionRepository.save(description2);
    }

    @AfterEach
    void tearDown() {
        descriptionRepository.deleteAll();
        tagRepository.deleteAll();
        spaceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("공간 등록 - 성공")
    @WithMockUser(username = "host", roles = "HOST")
    void success_registerSpace() throws Exception {
        // given
        String spaceJson = """
            {
                "name": "새로운 공간",
                "spaceCategory": "아트 공방",
                "useCategory": "촬영",
                "maxCapacity": 15,
                "address": "서울 강남구 테헤란로 124",
                "detailAddress": "홍대빌딩 3층",
                "pricePerHour": 25000,
                "description": "아트 스튜디오 공간입니다",
                "priceGuide": "시간당 25,000원",
                "facilityNotice": "조명장비 완비",
                "notice": "촬영 시 사전 문의 필수",
                "locationDescription": "홍대입구역 도보 3분",
                "refundPolicy": "48시간 전 취소 가능",
                "tags": ["촬영", "아트", "스튜디오"]
            }
            """;

        MockMultipartFile spaceData = new MockMultipartFile(
                "space", "", "application/json", spaceJson.getBytes());

        MockMultipartFile thumbnailFile = new MockMultipartFile(
                "thumbnailFile", "thumbnail.jpg", "image/jpeg", "thumbnail content".getBytes());

        // when & then
        mockMvc.perform(multipart("/api/spaces")
                        .file(spaceData)
                        .file(thumbnailFile)
                        .with(user(host1.getLoginId()).roles("HOST"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
                // .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("공간이 등록되었습니다")));
    }

    @Test
    @DisplayName("공간 단순 정보 조회 - 성공")
    void success_getSimpleInfo() throws Exception {
        // when & then
        mockMvc.perform(get("/api/spaces/{id}/simple", space1.getId())
                        .with(user(host1.getLoginId()).roles("HOST")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(space1.getName()))
                .andExpect(jsonPath("$.data.address").value("역삼동"))
                .andExpect(jsonPath("$.data.pricePerHour").value(space1.getPricePerHour()))
                .andExpect(jsonPath("$.data.thumbnailUrl").value(space1.getThumbnailUrl()));
    }

    @Test
    @DisplayName("공간 단순 정보 조회 - 존재하지 않는 공간")
    void getSimpleInfo_space_not_found() throws Exception {
        // when & then
        mockMvc.perform(get("/api/spaces/{id}/simple", 999L)
                        .with(user(host1.getLoginId()).roles("HOST")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(SpaceErrorCode.SPACE_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("공간 상세 정보 조회 - 성공")
    void success_getDetailInfo() throws Exception {
        // when & then
        mockMvc.perform(get("/api/spaces/{id}", space1.getId())
                        .with(user(host1.getLoginId()).roles("HOST")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(space1.getId()))
                .andExpect(jsonPath("$.data.name").value(space1.getName()))
                .andExpect(jsonPath("$.data.address").value(space1.getAddress()))
                .andExpect(jsonPath("$.data.detailAddress").value(space1.getDetailAddress()))
                .andExpect(jsonPath("$.data.pricePerHour").value(space1.getPricePerHour()))
                .andExpect(jsonPath("$.data.maxCapacity").value(space1.getMaxCapacity()))
                .andExpect(jsonPath("$.data.spaceCategory").value(space1.getSpaceCategory().getDisplayName()))
                .andExpect(jsonPath("$.data.useCategory").value(space1.getUseCategory().getDisplayName()))
                .andExpect(jsonPath("$.data.avgRating").value(space1.getAvgRating()))
                .andExpect(jsonPath("$.data.description").value(description1.getDescription()))
                .andExpect(jsonPath("$.data.priceGuide").value(description1.getPriceGuide()))
                .andExpect(jsonPath("$.data.facilityNotice").value(description1.getFacilityNotice()))
                .andExpect(jsonPath("$.data.notice").value(description1.getNotice()))
                .andExpect(jsonPath("$.data.locationDescription").value(description1.getLocationDescription()))
                .andExpect(jsonPath("$.data.refundPolicy").value(description1.getRefundPolicy()));
    }

    @Test
    @DisplayName("공간 상세 정보 조회 - 존재하지 않는 공간")
    void getDetailInfo_space_not_found() throws Exception {
        // when & then
        mockMvc.perform(get("/api/spaces/{id}", 999L)
                        .with(user(host1.getLoginId()).roles("HOST")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(SpaceErrorCode.SPACE_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("공간 전체 수정 - 성공")
    @WithMockUser(username = "host", roles = "HOST")
    void success_updateSpace() throws Exception {
        // given
        String updateJson = """
            {
                "name": "수정된 공간명",
                "spaceCategory": "카페",
                "useCategory": "단체 모임",
                "maxCapacity": 12,
                "address": "서울 강남구 테헤란로 124",
                "detailAddress": "수정된 상세주소",
                "pricePerHour": 18000,
                "description": "수정된 설명",
                "priceGuide": "수정된 가격 안내",
                "facilityNotice": "수정된 시설 안내",
                "notice": "수정된 주의사항",
                "locationDescription": "수정된 위치 설명",
                "refundPolicy": "수정된 환불 정책",
                "tags": ["수정", "테스트"]
            }
            """;

        MockMultipartFile spaceData = new MockMultipartFile(
                "space", "", "application/json", updateJson.getBytes());

        // when & then
        mockMvc.perform(multipart("/api/spaces/{id}", space1.getId())
                        .file(spaceData)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(user(host1.getLoginId()).roles("HOST"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
                //.andExpect(jsonPath("$.message").value("공간이 성공적으로 수정되었습니다."));
    }

    @Test
    @DisplayName("공간 전체 수정 - 권한 없음")
    @WithMockUser(username = "host", roles = "HOST")
    void updateSpace_unauthorized() throws Exception {
        // given
        String updateJson = """
            {
                "name": "수정된 공간명",
                "spaceCategory": "카페",
                "useCategory": "단체 모임",
                "maxCapacity": 12,
                "address": "서울 강남구 테헤란로 124",
                "detailAddress": "수정된 상세주소",
                "pricePerHour": 18000,
                "description": "수정된 설명",
                "priceGuide": "수정된 가격 안내",
                "facilityNotice": "수정된 시설 안내",
                "notice": "수정된 주의사항",
                "locationDescription": "수정된 위치 설명",
                "refundPolicy": "수정된 환불 정책",
                "tags": ["수정", "테스트"]
            }
            """;

        MockMultipartFile spaceData = new MockMultipartFile(
                "space", "", "application/json", updateJson.getBytes());

        // when & then (host2가 host1의 공간을 수정하려고 시도)
        mockMvc.perform(multipart("/api/spaces/{id}", space1.getId())
                        .file(spaceData)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .with(user(host2.getLoginId()).roles("HOST"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(SpaceErrorCode.NO_PERMISSION.getMessage()));
    }

/*    @Test
    @DisplayName("공간 기본 정보 부분 수정 - 성공")
    @WithMockUser(username = "host", roles = "HOST")
    void success_updateSpaceBasic() throws Exception {
        // given
        String updateJson = """
            {
                "name": "부분 수정된 공간명",
                "spaceCategory": "아트 공방",
                "useCategory": "촬영",
                "maxCapacity": 8,
                "address": "서울 강남구 테헤란로 124",
                "detailAddress": "부분 수정된 상세주소",
                "pricePerHour": 12000,
                "description": "기존 설명",
                "notice": "기존 주의사항",
                "refundPolicy": "기존 환불 정책"
            }
            """;

        // when & then
        mockMvc.perform(patch("/api/spaces/{id}/basic", space1.getId())
                        .with(user(host1.getLoginId()).roles("HOST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());
                // .andExpect(jsonPath("$.message").value("공간 기본 정보가 성공적으로 수정되었습니다."));
    }*/

/*    @Test
    @DisplayName("공간 설명 부분 수정 - 성공")
    @WithMockUser(username = "host", roles = "HOST")
    void success_updateSpaceDescription() throws Exception {
        // given
        String updateJson = """
            {
                "name": "기존 공간명",
                "description": "수정된 설명입니다",
                "priceGuide": "수정된 가격 안내",
                "facilityNotice": "수정된 시설 안내",
                "notice": "수정된 주의사항",
                "locationDescription": "수정된 위치 설명",
                "refundPolicy": "수정된 환불 정책"
            }
            """;

        // when & then
        mockMvc.perform(patch("/api/spaces/{id}/description", space1.getId())
                        .with(user(host1.getLoginId()).roles("HOST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());
                // .andExpect(jsonPath("$.message").value("공간 설명이 성공적으로 수정되었습니다."));
    }*/

/*    @Test
    @DisplayName("공간 태그 수정 - 성공")
    @WithMockUser(username = "host", roles = "HOST")
    void success_updateTags() throws Exception {
        // given
        String updateJson = """
            {
                "name": "기존 공간명",
                "description": "기존 설명",
                "notice": "기존 주의사항",
                "refundPolicy": "기존 환불 정책",
                "tags": ["새로운태그1", "새로운태그2", "새로운태그3"]
            }
            """;

        // when & then
        mockMvc.perform(patch("/api/spaces/{id}/tags", space1.getId())
                        .with(user(host1.getLoginId()).roles("HOST"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());
                //.andExpect(jsonPath("$.message").value("태그가 성공적으로 수정되었습니다."));
    }*/

    @Test
    @DisplayName("공간 삭제 - 성공")
    @WithMockUser(username = "host", roles = "HOST")
    void success_deleteSpace() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/spaces/{id}", space1.getId())
                        .with(user(host1.getLoginId()).roles("HOST")))
                .andExpect(status().isOk());
                //.andExpect(jsonPath("$.message").value("공간이 성공적으로 삭제되었습니다."));
    }

    @Test
    @DisplayName("공간 삭제 - 권한 없음")
    void deleteSpace_unauthorized() throws Exception {
        // when & then (host2가 host1의 공간을 삭제하려고 시도)
        mockMvc.perform(delete("/api/spaces/{id}", space1.getId())
                        .with(user(host2.getLoginId()).roles("HOST")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(SpaceErrorCode.NO_PERMISSION.getMessage()));
    }

    @Test
    @DisplayName("공간 삭제 - 존재하지 않는 공간")
    void deleteSpace_space_not_found() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/spaces/{id}", 999L)
                        .with(user(host1.getLoginId()).roles("HOST")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(SpaceErrorCode.SPACE_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("공간 등록 - 필수 필드 누락")
    void registerSpace_missing_required_fields() throws Exception {
        // given - name 필드가 누락된 JSON
        String invalidSpaceJson = """
            {
                "spaceCategory": "아트 공방",
                "useCategory": "촬영",
                "maxCapacity": 15,
                "address": "서울시 마포구 홍대동",
                "detailAddress": "홍대빌딩 3층",
                "pricePerHour": 25000,
                "description": "아트 스튜디오 공간입니다",
                "notice": "촬영 시 사전 문의 필수",
                "refundPolicy": "48시간 전 취소 가능"
            }
            """;

        MockMultipartFile spaceData = new MockMultipartFile(
                "space", "", "application/json", invalidSpaceJson.getBytes());

        // when & then
        mockMvc.perform(multipart("/api/spaces")
                        .file(spaceData)
                        .with(user(host1.getLoginId()).roles("HOST"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }
}

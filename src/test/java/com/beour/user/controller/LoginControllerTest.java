package com.beour.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.beour.global.exception.error.errorcode.UserErrorCode;
import com.beour.user.entity.User;
import com.beour.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
        User user = User.builder()
            .name("유저1")
            .nickname("user1")
            .email("user1@gmail.com")
            .loginId("user1")
            .password("user1")
            .phone("01012345678")
            .role("GUEST")
            .build();
        userRepository.save(user);

        User deleteUser = User.builder()
            .name("탈퇴유저")
            .nickname("delete1")
            .email("delete1@gmail.com")
            .loginId("delete1")
            .password("delete1")
            .phone("01012345678")
            .role("GUEST")
            .build();
        deleteUser.softDelete();
        userRepository.save(deleteUser);
    }

    @Test
    @DisplayName("아이디 찾기 - 회원 없을 경우")
    void findLogId_user_not_found() throws Exception {
        //given
        String requestJson = """
            {
                "name": "user1",
                "email": "test@gmail.com",
                "phone": "01012345678"
            }
        """;

        //when then
        mockMvc.perform(post("/api/users/find-login-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(UserErrorCode.MEMBER_NOT_FOUND.getMessage()));

    }

    @Test
    @DisplayName("아이디 찾기 - 탈퇴한 회원일 경우")
    void findLogId_user_deleted() throws Exception {
        //given
        String requestJson = """
            {
                "name": "탈퇴유저",
                "email": "delete1@gmail.com",
                "phone": "01012345678"
            }
        """;

        //when then
        mockMvc.perform(post("/api/users/find-login-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(UserErrorCode.MEMBER_NOT_FOUND.getMessage()));

    }

    @Test
    @DisplayName("아이디 찾기 - 성공")
    void success_findLogId() throws Exception {
        //given
        String requestJson = """
            {
                "name": "유저1",
                "email": "user1@gmail.com",
                "phone": "01012345678"
            }
        """;

        //when then
        mockMvc.perform(post("/api/users/find-login-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.loginId").value("user1"));

    }

    @Test
    @DisplayName("비밀번호 재발급 - 회원 없을 경우")
    void resetPassword_user_not_found() throws Exception {
        //given
        String requestJson = """
            {
                "loginId": "user1",
                "name": "user1",
                "email": "test@gmail.com",
                "phone": "01012345678"
            }
        """;

        //when then
        mockMvc.perform(post("/api/users/reset-pw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(UserErrorCode.MEMBER_NOT_FOUND.getMessage()));

    }

    @Test
    @DisplayName("비밀번호 재발급 - 탈퇴한 회원일 경우")
    void resetPassword_user_deleted() throws Exception {
        //given
        String requestJson = """
            {
                "loginId": "delete1",
                "name": "탈퇴유저",
                "email": "delete1@gmail.com",
                "phone": "01012345678"
            }
        """;

        //when then
        mockMvc.perform(post("/api/users/reset-pw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(UserErrorCode.MEMBER_NOT_FOUND.getMessage()));

    }

    @Test
    @DisplayName("비밀번호 재발급 - 성공")
    void success_resetPassword() throws Exception {
        //given
        String requestJson = """
            {
                "loginId": "user1",
                "name": "유저1",
                "email": "user1@gmail.com",
                "phone": "01012345678"
            }
        """;

        //when then
        mockMvc.perform(post("/api/users/reset-pw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
            )
            .andExpect(status().isOk());
    }


}
package com.beour.user.service;

import com.beour.global.exception.exceptionType.DuplicateUserInfoException;
import com.beour.user.dto.SignupRequestDto;
import com.beour.user.entity.User;
import com.beour.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SignupService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User create(SignupRequestDto dto) {
        checkValidUser(dto);
        dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        User user = User.createFrom(dto);

        return userRepository.save(user);
    }

    private void checkValidUser(SignupRequestDto dto) {
        checkDuplicateWithAdminId(dto);
        checkLoginIdDuplicate(dto.getLoginId());
        checkNicknameDuplicate(dto.getNickname());
    }

    private static void checkDuplicateWithAdminId(SignupRequestDto dto) {
        if (dto.getLoginId().equals("admin")) {
            throw new DuplicateUserInfoException("사용할 수 없는 아이디입니다.");
        }
    }

    public boolean checkLoginIdDuplicate(String loginId) {
        Boolean isUserExist = userRepository.existsByLoginIdAndDeletedAtIsNull(loginId);

        if (isUserExist) {
            throw new DuplicateUserInfoException("이미 사용중인 아이디입니다.");
        }

        return false;
    }

    public boolean checkNicknameDuplicate(String nickname) {
        Boolean isUserExist = userRepository.existsByNicknameAndDeletedAtIsNull(nickname);

        if (isUserExist) {
            throw new DuplicateUserInfoException("이미 사용중인 닉네임입니다.");
        }

        return false;
    }

}

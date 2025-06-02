package com.beour.user.service;

import com.beour.global.exception.exceptionType.UserNotFoundException;
import com.beour.global.response.ApiResponse;
import com.beour.user.dto.FindLoginIdRequestDto;
import com.beour.user.dto.FindLoginIdResponseDto;
import com.beour.user.dto.ResetPasswordRequestDto;
import com.beour.user.dto.ResetPasswordResponseDto;
import com.beour.user.entity.User;
import com.beour.user.repository.UserRepository;
import java.security.SecureRandom;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LoginService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public ApiResponse<FindLoginIdResponseDto> findLoginId(FindLoginIdRequestDto dto) {
    User user = userRepository.findByNameAndPhoneAndEmail(dto.getName(), dto.getPhone(),
        dto.getEmail()).orElseThrow(
        () -> new UserNotFoundException("일치하는 회원을 찾을 수 없습니다.")
    );
    checkDeletedUser(user);

    return ApiResponse.ok(new FindLoginIdResponseDto(user.getLoginId()));
  }

  @Transactional
  public ApiResponse<ResetPasswordResponseDto> resetPassword(ResetPasswordRequestDto dto) {
    if (isExistUser(dto)) {
      String tempPassword = generateTempPassword();
      String encode = bCryptPasswordEncoder.encode(tempPassword);
      userRepository.updatePasswordByLoginId(dto.getLoginId(), encode);

      return ApiResponse.ok(new ResetPasswordResponseDto(tempPassword));
    }

    throw new UserNotFoundException("일치하는 회원을 찾을 수 없습니다.");
  }

  private Boolean isExistUser(ResetPasswordRequestDto dto) {
    User userByLoginID = userRepository.findByLoginId(dto.getLoginId()).orElseThrow(
        () -> new UserNotFoundException("일치하는 회원을 찾을 수 없습니다.")
    );

    User userByNamePhoneEmail = userRepository.findByNameAndPhoneAndEmail(dto.getName(),
        dto.getPhone(), dto.getEmail()).orElseThrow(
        () -> new UserNotFoundException("일치하는 회원을 찾을 수 없습니다.")
    );

    checkDeletedUser(userByLoginID);
    checkDeletedUser(userByNamePhoneEmail);

    if (userByLoginID.getId() == userByNamePhoneEmail.getId()) {
      return true;
    }

    throw new UserNotFoundException("일치하는 회원을 찾을 수 없습니다.");
  }

  private static void checkDeletedUser(User user) {
    if(user.isDeleted()){
      throw new UserNotFoundException("탈퇴한 회원입니다.");
    }
  }

  private String generateTempPassword() {
    int length = 10;
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    StringBuilder sb = new StringBuilder();

    Random random = new SecureRandom();
    for (int i = 0; i < length; i++) {
      sb.append(chars.charAt(random.nextInt(chars.length())));
    }

    return sb.toString();
  }
}

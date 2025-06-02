package com.beour.user.service;

import com.beour.global.exception.exceptionType.DuplicateUserInfoException;
import com.beour.global.exception.exceptionType.InvalidCredentialsException;
import com.beour.global.exception.exceptionType.InvalidFormatException;
import com.beour.global.exception.exceptionType.UserNotFoundException;
import com.beour.user.dto.SignupRequestDto;
import com.beour.user.entity.User;
import com.beour.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RequiredArgsConstructor
@Service
public class SignupService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  //todo: 관리자 계정 만들면 관리자 아이디 체크 로직 추가
  public User create(SignupRequestDto dto) {
    validDuplicateUser(dto);
    checkRoleInvalidRequest(dto.getRole());
    dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
    User user = User.createFrom(dto);

    return userRepository.save(user);
  }

  private void validDuplicateUser(SignupRequestDto dto) {
    isExistLoginId(dto.getLoginId());
    isExistNickname(dto.getNickname());
  }

  private void checkRoleInvalidRequest(String role){
    if(!role.equals("HOST") && !role.equals("GUEST")){
      throw new InvalidFormatException("HOST 또는 GUEST만 입력해주세요.");
    }
  }

  public boolean checkLoginIdDuplicate(String loginId) {
    if(loginId.isBlank()){
      throw new InvalidFormatException("아이디를 입력해주세요.");
    }

    isExistLoginId(loginId);

    return false;
  }

  private boolean isExistLoginId(String loginId) {
    Boolean isUserExist = userRepository.existsByLoginId(loginId);

    if(isUserExist){
      User user = userRepository.findByLoginId(loginId).orElseThrow(
          () -> new UserNotFoundException("존재하지 않는 사용자입니다.")
      );

      if(user.isDeleted()){
        return false;
      }

      throw new DuplicateUserInfoException("이미 사용중인 id입니다.");
    }

    return false;
  }

  public boolean checkNicknameDuplicate(String nickname) {
    if(nickname.isBlank()){
      throw new InvalidFormatException("닉네임을 입력해주세요.");
    }

    isExistNickname(nickname);
    return false;
  }

  private boolean isExistNickname(String nickname) {
    Boolean isUserExist = userRepository.existsByNickname(nickname);

    if(isUserExist){
      User user = userRepository.findByNickname(nickname).orElseThrow(
          () -> new UserNotFoundException("존재하지 않는 사용자입니다.")
      );

      if(user.isDeleted()){
        return false;
      }

      throw new DuplicateUserInfoException("이미 사용중인 닉네임입니다.");
    }

    return false;
  }
}

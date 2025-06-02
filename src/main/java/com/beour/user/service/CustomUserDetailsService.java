package com.beour.user.service;

import com.beour.global.exception.exceptionType.UserNotFoundException;
import com.beour.user.dto.CustomUserDetails;
import com.beour.user.entity.User;
import com.beour.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String loginId) {

    User user = userRepository.findByLoginId(loginId).orElseThrow(
        () -> new UserNotFoundException("일치하는 회원이 없습니다.")
    );

    if(user.isDeleted()){
      throw new UserNotFoundException("탈퇴한 회원입니다.");
    }

    return new CustomUserDetails(user);
  }

}

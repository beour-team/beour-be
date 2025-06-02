package com.beour.user.service;

import com.beour.global.exception.exceptionType.InvalidCredentialsException;
import com.beour.global.exception.exceptionType.InvalidFormatException;
import com.beour.global.exception.exceptionType.UserNotFoundException;
import com.beour.user.dto.ChangePasswordRequestDto;
import com.beour.user.dto.CustomUserDetails;
import com.beour.user.dto.UpdateUserInfoRequestDto;
import com.beour.user.dto.UserInformationDetailResponseDto;
import com.beour.user.dto.UserInformationSimpleResponseDto;
import com.beour.user.entity.User;
import com.beour.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MyInformationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void updatePassword(ChangePasswordRequestDto changePasswordRequestDto){
        User user = findUserFromToken();

        String newPassword = bCryptPasswordEncoder.encode(changePasswordRequestDto.getNewPassword());
        user.updatePassword(newPassword);
    }

    public UserInformationSimpleResponseDto getUserInformationSimple(){
        User user = findUserFromToken();

        UserInformationSimpleResponseDto dto = new UserInformationSimpleResponseDto();
        dto.setUserName(user.getName());
        dto.setUserEmail(user.getEmail());

        return dto;
    }

    public UserInformationDetailResponseDto getUserInformationDetail(){
        User user = findUserFromToken();

        UserInformationDetailResponseDto dto = new UserInformationDetailResponseDto();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setNickName(user.getNickname());
        dto.setPhoneNum(user.getPhone());

        return dto;
    }

    @Transactional
    public void updateUserInfo(UpdateUserInfoRequestDto requestDto){
        User user = findUserFromToken();

        if(!requestDto.getNewNickname().isEmpty()){
            user.updateNickname(requestDto.getNewNickname());
        }

        if(!requestDto.getNewPhone().isEmpty()){
            user.updatePhone(requestDto.getNewPhone());
        }

        if(requestDto.getNewNickname().isEmpty() && requestDto.getNewPhone().isEmpty()){
            throw new InvalidFormatException("수정할 정보를 입력해주세요.");
        }
    }

    @Transactional
    public void deleteUser(){
        User user = findUserFromToken();
        user.softDelete();
    }

    private User findUserFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            throw new InvalidCredentialsException("인증된 유저가 없습니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findByLoginId(userDetails.getUsername()).orElseThrow(
            () -> new UserNotFoundException("해당 유저를 찾을 수 없습니다.")
        );

        if(user.isDeleted()){
            throw new UserNotFoundException("해당 유저를 찾을 수 없습니다.");
        }

        return user;
    }

}

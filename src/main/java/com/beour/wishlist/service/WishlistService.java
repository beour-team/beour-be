package com.beour.wishlist.service;

import com.beour.global.exception.exceptionType.DuplicateLikesException;
import com.beour.global.exception.exceptionType.InvalidCredentialsException;
import com.beour.global.exception.exceptionType.LikesNotFoundException;
import com.beour.global.exception.exceptionType.SpaceNotFoundException;
import com.beour.global.exception.exceptionType.UserNotFoundException;
import com.beour.space.domain.entity.Space;
import com.beour.space.domain.repository.SpaceRepository;
import com.beour.space.guest.dto.SpaceListSpaceResponseDto;
import com.beour.user.dto.CustomUserDetails;
import com.beour.user.entity.User;
import com.beour.user.repository.UserRepository;
import com.beour.wishlist.entity.Like;
import com.beour.wishlist.repository.LikeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class WishlistService {

    private final LikeRepository likeRepository;
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;

    public Like addSpaceToWishList(Long spaceId) {
        User user = findUserFromToken();
        Space space = getSpace(spaceId);

        if(isExistInWishList(spaceId, user)){
            throw new DuplicateLikesException("이미 찜 목록에 있습니다.");
        }

        Like like = Like.builder()
            .space(space)
            .user(user)
            .build();

        Like saved = likeRepository.save(like);

        return saved;
    }

    private boolean isExistInWishList(Long spaceId, User user) {
        if(likeRepository.existsByUserIdAndSpaceId(user.getId(), spaceId)){
            return true;
        }

        return false;
    }

    @Transactional
    public void deleteSpaceFromWishList(Long spaceId) {
        User user = findUserFromToken();
        Space space = getSpace(spaceId);

        Like like = likeRepository.findByUserIdAndSpaceId(user.getId(), space.getId()).orElseThrow(
            () -> new LikesNotFoundException("찜 목록에 존재하지 않습니다.")
        );
        if(like.isDeleted()){
            throw new LikesNotFoundException("찜 목록에 존재하지 않습니다.");
        }

        like.softDelete();
    }

    private Space getSpace(Long spaceId) {
        Space space = spaceRepository.findById(spaceId).orElseThrow(
            () -> new SpaceNotFoundException("해당 공간은 존재하지 않습니다.")
        );

        if(space.isDeleted()){
            throw new SpaceNotFoundException("해당 공간은 존재하지 않습니다.");
        }
        return space;
    }

    public List<SpaceListSpaceResponseDto> getWishlist(){
        User user = findUserFromToken();
        List<Like> whisList = likeRepository.findByUserIdAndDeletedAtIsNull(user.getId());

        if(whisList.isEmpty()){
            throw new LikesNotFoundException("찜 목록이 비어있습니다.");
        }

        return whisList.stream()
            .map(like -> SpaceListSpaceResponseDto.of(like.getSpace(), true))
            .collect(Collectors.toList());
    }

    private User findUserFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidCredentialsException("인증된 유저가 없습니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userRepository.findByLoginId(userDetails.getUsername()).orElseThrow(
            () -> new UserNotFoundException("해당 유저를 찾을 수 없습니다.")
        );

        if (user.isDeleted()) {
            throw new UserNotFoundException("해당 유저를 찾을 수 없습니다.");
        }

        return user;
    }

}

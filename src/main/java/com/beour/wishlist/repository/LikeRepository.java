package com.beour.wishlist.repository;

import com.beour.wishlist.entity.Like;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndSpaceId(Long userId, Long spaceId);

    Optional<Like> findByUserIdAndSpaceId(Long userId, Long spaceId);

    List<Like> findByUserIdAndDeletedAtIsNull(Long userId);
}


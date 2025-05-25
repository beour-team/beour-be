package com.beour.review.domain.repository;

import com.beour.review.domain.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    Optional<ReviewComment> findByReviewIdAndDeletedAtIsNull(Long reviewId);
}

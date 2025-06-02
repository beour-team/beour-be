package com.beour.review.domain.repository;

import com.beour.review.domain.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
}

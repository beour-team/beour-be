package com.beour.review.domain.repository;

import com.beour.review.domain.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    void deleteByReviewId(Long reviewId);
}

package com.beour.review.domain.repository;

import com.beour.review.domain.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    void deleteByReviewId(Long reviewId);
    List<ReviewImage> findByReviewId(Long reviewId);
}

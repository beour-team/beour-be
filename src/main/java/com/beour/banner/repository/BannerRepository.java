package com.beour.banner.repository;

import com.beour.banner.entity.Banner;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    @Query("SELECT b FROM Banner b WHERE b.deletedAt IS NULL AND b.isActive = true AND b.startDate <= :today AND b.endDate >= :today")
    List<Banner> findValidBanners(@Param("today") LocalDate today);

}

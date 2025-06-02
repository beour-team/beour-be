package com.beour.space.domain.repository;

import com.beour.space.domain.entity.AvailableTime;
import com.beour.space.domain.entity.Space;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AvailableTimeRepository extends JpaRepository<AvailableTime, Long> {

    void deleteBySpace(Space space);

    Optional<AvailableTime> findBySpaceIdAndDateAndDeletedAtIsNull(Long spaceId, LocalDate date);

}

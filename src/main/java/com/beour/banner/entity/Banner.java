package com.beour.banner.entity;

import com.beour.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Banner extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgUrl;
    private String linkUrl;
    private String title;
    private boolean isActive;

    @Column(unique = true)
    private int displayOrder;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    public Banner(String imgUrl, String linkUrl, String title, boolean isActive, int displayOrder, LocalDate startDate, LocalDate endDate){
        this.imgUrl = imgUrl;
        this.linkUrl = linkUrl;
        this.title = title;
        this.isActive = isActive;
        this.displayOrder = displayOrder;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}

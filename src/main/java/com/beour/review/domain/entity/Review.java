package com.beour.review.domain.entity;

import com.beour.global.entity.BaseTimeEntity;
import com.beour.reservation.commons.entity.Reservation;
import com.beour.space.domain.entity.Space;
import com.beour.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE review SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Space space;

    @ManyToOne(fetch = FetchType.LAZY)
    private User guest;

    private int rating;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewImage> reviewImages;

    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL)
    private ReviewComment hostComment;

    public void updateRating(int rating) {
        this.rating = rating;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}

package com.beour.review.domain.entity;

import com.beour.global.entity.BaseTimeEntity;
import com.beour.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE review_comment SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class ReviewComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    private User host;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime deletedAt;
}

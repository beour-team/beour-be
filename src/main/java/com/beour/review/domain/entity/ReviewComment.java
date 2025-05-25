package com.beour.review.domain.entity;

import com.beour.global.entity.BaseTimeEntity;
import com.beour.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private User host;

    @Column(length = 1000)
    private String content;

    private LocalDate deletedAt;

    public void updateContent(String content) {
        this.content = content;
    }

    public void delete() {
        this.deletedAt = LocalDate.now();
    }
}

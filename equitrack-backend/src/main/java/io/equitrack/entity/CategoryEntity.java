package io.equitrack.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "tbl_categories")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String type;

    private String icon;

    //many categories can interact to only one profile, bruh bali, profile -> *categories
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private ProfileEntity profile;
}

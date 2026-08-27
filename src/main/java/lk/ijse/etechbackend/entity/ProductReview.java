package lk.ijse.etechbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_reviews",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_user_product_review", columnNames = {"product_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_reviews_product", columnList = "product_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview {

    @Id
    @Column(name = "id", length = 50)
    private String id; // e.g. "REV-10001"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reviews_product"))
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reviews_user"))
    private User user;

    @Column(name = "user_name", length = 100, nullable = false)
    private String userName;

    @Column(name = "user_email", length = 150, nullable = false)
    private String userEmail;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

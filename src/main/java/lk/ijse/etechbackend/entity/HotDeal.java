package lk.ijse.etechbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hot_deals", indexes = {
        @Index(name = "idx_hot_deals_product", columnList = "product_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotDeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_hot_deals_product"))
    private Product product;

    @Column(name = "badge", length = 50, nullable = false)
    @Builder.Default
    private String badge = "HOT DEAL";

    @Column(name = "promo_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal promoPrice;

    @Column(name = "original_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal originalPrice;

    @Column(name = "discount_percent", nullable = false)
    @Builder.Default
    private Integer discountPercent = 0;

    @Column(name = "duration_seconds", nullable = false)
    @Builder.Default
    private Integer durationSeconds = 86400;

    @Column(name = "timer_updated_at")
    @Builder.Default
    private LocalDateTime timerUpdatedAt = LocalDateTime.now();

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

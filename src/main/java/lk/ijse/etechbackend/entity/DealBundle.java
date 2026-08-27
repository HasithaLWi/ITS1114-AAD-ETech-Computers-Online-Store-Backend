package lk.ijse.etechbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "deal_bundles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealBundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "badge", length = 50, nullable = false)
    @Builder.Default
    private String badge = "BEST DEAL";

    @Column(name = "eyebrow", length = 100)
    @Builder.Default
    private String eyebrow = "FEATURED DEAL";

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "subtitle", length = 255)
    private String subtitle;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "price", precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "original_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal originalPrice;

    @Column(name = "target_quota")
    @Builder.Default
    private Integer targetQuota = 20;

    @Column(name = "sold_count")
    @Builder.Default
    private Integer soldCount = 0;

    @Column(name = "duration_seconds", nullable = false)
    @Builder.Default
    private Integer durationSeconds = 86400;

    @Column(name = "timer_updated_at")
    @Builder.Default
    private LocalDateTime timerUpdatedAt = LocalDateTime.now();

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "bundle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<BundleItem> bundleItems = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void addBundleItem(BundleItem item) {
        if (bundleItems == null) {
            bundleItems = new ArrayList<>();
        }
        bundleItems.add(item);
        item.setBundle(this);
    }

    public void removeBundleItem(BundleItem item) {
        if (bundleItems != null) {
            bundleItems.remove(item);
            item.setBundle(null);
        }
    }
}

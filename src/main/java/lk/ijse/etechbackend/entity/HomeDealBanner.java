package lk.ijse.etechbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "home_deal_banner")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeDealBanner {

    @Id
    @Column(name = "id")
    @Builder.Default
    private Integer id = 1;

    @Column(name = "deal_tag", length = 100, nullable = false)
    @Builder.Default
    private String dealTag = "WEEKEND TECH DEAL";

    @Column(name = "heading", columnDefinition = "TEXT", nullable = false)
    private String heading;

    @Column(name = "subtitle", length = 255)
    private String subtitle;

    @Column(name = "button_text", length = 100)
    @Builder.Default
    private String buttonText = "View All Deals";

    @Column(name = "button_url", length = 255)
    @Builder.Default
    private String buttonUrl = "#deals";

    @Column(name = "background_image", columnDefinition = "TEXT")
    private String backgroundImage;

    @Column(name = "duration_seconds", nullable = false)
    @Builder.Default
    private Integer durationSeconds = 86400;

    @Column(name = "timer_updated_at")
    @Builder.Default
    private LocalDateTime timerUpdatedAt = LocalDateTime.now();

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

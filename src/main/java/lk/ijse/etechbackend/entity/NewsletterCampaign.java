package lk.ijse.etechbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "newsletter_campaigns", indexes = {
        @Index(name = "idx_campaigns_sent_at", columnList = "sent_at"),
        @Index(name = "idx_campaigns_category", columnList = "category")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsletterCampaign {

    @Id
    @Column(name = "id", length = 100)
    private String id; // e.g. "camp_20260825_01"

    @Column(name = "subject", length = 255, nullable = false)
    private String subject;

    @Column(name = "preheader", length = 255)
    private String preheader;

    @Column(name = "category", length = 50, nullable = false)
    @Builder.Default
    private String category = "GENERAL_NEWS";

    @Column(name = "target_segment", length = 50, nullable = false)
    @Builder.Default
    private String targetSegment = "ALL_ACTIVE";

    @Column(name = "content_html", columnDefinition = "LONGTEXT")
    private String contentHtml;

    @CreationTimestamp
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "recipients_count", nullable = false)
    @Builder.Default
    private Integer recipientsCount = 0;

    @Column(name = "status", length = 50, nullable = false)
    @Builder.Default
    private String status = "DELIVERED";

    @Column(name = "open_rate", precision = 4, scale = 1)
    @Builder.Default
    private BigDecimal openRate = BigDecimal.ZERO;

    @Column(name = "click_rate", precision = 4, scale = 1)
    @Builder.Default
    private BigDecimal clickRate = BigDecimal.ZERO;

    @Column(name = "author_name", length = 100, nullable = false)
    @Builder.Default
    private String authorName = "Admin Team";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

package lk.ijse.etechbackend.entity;

import jakarta.persistence.*;
import lk.ijse.etechbackend.converter.JsonListConverter;
import lk.ijse.etechbackend.enumiration.SubscriberSource;
import lk.ijse.etechbackend.enumiration.SubscriberStatus;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "newsletter_subscribers", indexes = {
        @Index(name = "idx_subscribers_email", columnList = "email"),
        @Index(name = "idx_subscribers_status", columnList = "status"),
        @Index(name = "idx_subscribers_source", columnList = "source")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsletterSubscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @Column(name = "name", length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private SubscriberStatus status = SubscriberStatus.SUBSCRIBED;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 50, nullable = false)
    @Builder.Default
    private SubscriberSource source = SubscriberSource.STOREFRONT_BANNER;

    @Convert(converter = JsonListConverter.class)
    @Column(name = "tags_json", columnDefinition = "JSON")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "subscribed_at", updatable = false)
    private LocalDateTime subscribedAt;

    @Column(name = "unsubscribed_at")
    private LocalDateTime unsubscribedAt;

    @Column(name = "last_campaign_sent_at")
    private LocalDateTime lastCampaignSentAt;

    @Column(name = "ip_address", length = 45)
    @Builder.Default
    private String ipAddress = "127.0.0.1";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

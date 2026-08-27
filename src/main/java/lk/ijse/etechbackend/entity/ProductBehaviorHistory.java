package lk.ijse.etechbackend.entity;

import jakarta.persistence.*;
import lk.ijse.etechbackend.converter.JsonObjectConverter;
import lk.ijse.etechbackend.enumiration.ProductBehaviorActor;
import lk.ijse.etechbackend.enumiration.ProductBehaviorEventType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_behavior_history", indexes = {
        @Index(name = "idx_behavior_product", columnList = "product_id"),
        @Index(name = "idx_behavior_event", columnList = "event_type")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBehaviorHistory {

    @Id
    @Column(name = "id", length = 100)
    private String id; // e.g. "pbe-1723824000-123"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_behavior_product"))
    private Product product;

    @Column(name = "product_name", length = 255, nullable = false)
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 50, nullable = false)
    private ProductBehaviorEventType eventType;

    @Column(name = "previous_value", length = 255)
    private String previousValue;

    @Column(name = "new_value", length = 255)
    private String newValue;

    @Column(name = "trigger_reason", columnDefinition = "TEXT")
    private String triggerReason;

    @Convert(converter = JsonObjectConverter.class)
    @Column(name = "metrics_snapshot", columnDefinition = "JSON")
    private Object metricsSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor", length = 50, nullable = false)
    @Builder.Default
    private ProductBehaviorActor actor = ProductBehaviorActor.SYSTEM_AUTO_RULE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

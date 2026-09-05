package lk.ijse.etechbackend.entity;

import jakarta.persistence.*;
import lk.ijse.etechbackend.enumiration.BadgeRuleType;
import lk.ijse.etechbackend.enumiration.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "badges", indexes = {
        @Index(name = "idx_badges_slug", columnList = "slug"),
        @Index(name = "idx_badges_name", columnList = "name")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Badge {

    @Id
    @Column(name = "id", length = 50)
    private String id; // e.g. "bdg-bestseller", "bdg-hotdeal"

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "slug", length = 100, nullable = false, unique = true)
    private String slug;

    @Column(name = "color_key", length = 30, nullable = false)
    @Builder.Default
    private String colorKey = "blue";

    @Column(name = "color_hex", length = 20, nullable = false)
    @Builder.Default
    private String colorHex = "#2563eb";

    @Column(name = "purpose", columnDefinition = "TEXT")
    private String purpose;

    @Column(name = "standard_description", columnDefinition = "TEXT")
    private String standardDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", length = 20, nullable = false)
    @Builder.Default
    private BadgeRuleType ruleType = BadgeRuleType.manual;

    @Column(name = "criteria", length = 100)
    @Builder.Default
    private String criteria = "custom";

    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 10;

    @Column(name = "is_system_default")
    @Builder.Default
    private Boolean isSystemDefault = false;

    @Column(name = "can_edit")
    @Builder.Default
    private Boolean canEdit = true;

    @Column(name = "can_delete")
    @Builder.Default
    private Boolean canDelete = true;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Product> products = new HashSet<>();
}

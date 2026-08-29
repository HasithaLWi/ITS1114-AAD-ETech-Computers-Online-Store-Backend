package lk.ijse.etechbackend.entity;

import jakarta.persistence.*;
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
@Table(name = "categories", indexes = {
        @Index(name = "idx_categories_slug", columnList = "slug")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @Column(name = "id", length = 50)
    private String id; // e.g. "cat-laptops", "cat-components"

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "slug", length = 100, nullable = false, unique = true)
    private String slug;

    @Column(name = "icon", length = 50)
    @Builder.Default
    private String icon = "📦";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "featured")
    @Builder.Default
    private Boolean featured = false;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Product> products = new HashSet<>();
}

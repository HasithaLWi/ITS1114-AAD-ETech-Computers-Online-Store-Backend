package lk.ijse.etechbackend.entity;

import jakarta.persistence.*;
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
@Table(name = "brands", indexes = {
        @Index(name = "idx_brands_slug", columnList = "slug"),
        @Index(name = "idx_brands_featured", columnList = "featured")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Brand {

    @Id
    @Column(name = "id", length = 50)
    private String id; // e.g. "brd-asus", "brd-intel"

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "slug", length = 100, nullable = false, unique = true)
    private String slug;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    @Column(name = "country", length = 100)
    @Builder.Default
    private String country = "Global";

    @Column(name = "founded_year", length = 20)
    private String foundedYear;

    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @Column(name = "tagline", length = 255)
    private String tagline;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "featured")
    @Builder.Default
    private Boolean featured = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status =  Status.ACTIVE;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Product> products = new HashSet<>();
}

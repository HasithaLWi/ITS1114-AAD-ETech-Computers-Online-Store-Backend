package lk.ijse.etechbackend.entity;

import jakarta.persistence.*;
import lk.ijse.etechbackend.enumiration.Status;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_products_sku", columnList = "sku"),
        @Index(name = "idx_products_category", columnList = "category_id"),
        @Index(name = "idx_products_price", columnList = "price")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @JoinColumn(name = "brand_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Brand brand;

    @JoinColumn(name = "badge_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Badge badge;

    @Column(name = "price", precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "original_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal originalPrice;

    @Column(name = "rating", precision = 2, scale = 1)
    @Builder.Default
    private BigDecimal rating = new BigDecimal("5.0");

    @Column(name = "reviews_count")
    @Builder.Default
    private Integer reviewsCount = 0;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "full_description", columnDefinition = "LONGTEXT")
    private String fullDescription;

    @Column(name = "sku", length = 100, nullable = false, unique = true)
    private String sku;

    @Column(name = "warranty", length = 150)
    @Builder.Default
    private String warranty = "2-Year Warranty";

    @Column(name = "alert_enabled")
    @Builder.Default
    private Boolean alertEnabled = true;

    @Column(name = "low_stock_margin")
    @Builder.Default
    private Integer lowStockMargin = 5;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Specs> specs = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Features> features = new ArrayList<>();


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BranchInventory> branchInventories = new ArrayList<>();


    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status productStatus = Status.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods for bidirectional relationships
    public void addImage(ProductImage image) {
        if (images == null) {
            images = new ArrayList<>();
        }
        images.add(image);
        image.setProduct(this);
    }

    public void removeImage(ProductImage image) {
        if (images != null) {
            images.remove(image);
            image.setProduct(null);
        }
    }

    public void addBranchInventory(BranchInventory inventory) {
        if (branchInventories == null) {
            branchInventories = new ArrayList<>();
        }
        branchInventories.add(inventory);
        inventory.setProduct(this);
    }

    public void removeBranchInventory(BranchInventory inventory) {
        if (branchInventories != null) {
            branchInventories.remove(inventory);
            inventory.setProduct(null);
        }
    }

    public void addSpec(Specs spec) {
        if (specs == null) {
            specs = new ArrayList<>();
        }
        specs.add(spec);
        spec.setProduct(this);
    }

    public void removeSpec(Specs spec) {
        if (specs != null) {
            specs.remove(spec);
            spec.setProduct(null);
        }
    }

    public void addFeature(Features feature) {
        if (features == null) {
            features = new ArrayList<>();
        }
        features.add(feature);
        feature.setProduct(this);
    }

    public void removeFeature(Features feature) {
        if (features != null) {
            features.remove(feature);
            feature.setProduct(null);
        }
    }
}

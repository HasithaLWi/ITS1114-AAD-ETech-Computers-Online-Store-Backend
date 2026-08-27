package lk.ijse.etechbackend.entity;

import jakarta.persistence.*;
import lk.ijse.etechbackend.converter.JsonListConverter;
import lk.ijse.etechbackend.converter.JsonMapConverter;
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
        @Index(name = "idx_products_category", columnList = "category_slug"),
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

    @Column(name = "category_slug", length = 100, nullable = false)
    private String categorySlug;

    @Column(name = "brand", length = 100)
    @Builder.Default
    private String brand = "";

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

    @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "full_description", columnDefinition = "LONGTEXT")
    private String fullDescription;

    @Column(name = "sku", length = 100, nullable = false, unique = true)
    private String sku;

    @Column(name = "badge", length = 50)
    @Builder.Default
    private String badge = "";

    @Column(name = "warranty", length = 150)
    @Builder.Default
    private String warranty = "2-Year Warranty";

    @Column(name = "alert_enabled")
    @Builder.Default
    private Boolean alertEnabled = true;

    @Column(name = "low_stock_margin")
    @Builder.Default
    private Integer lowStockMargin = 5;

    @Convert(converter = JsonMapConverter.class)
    @Column(name = "specs_json", columnDefinition = "JSON")
    @Builder.Default
    private Map<String, String> specs = new HashMap<>();

    @Convert(converter = JsonListConverter.class)
    @Column(name = "features_json", columnDefinition = "JSON")
    @Builder.Default
    private List<String> features = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BranchInventory> branchInventories = new ArrayList<>();

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
}

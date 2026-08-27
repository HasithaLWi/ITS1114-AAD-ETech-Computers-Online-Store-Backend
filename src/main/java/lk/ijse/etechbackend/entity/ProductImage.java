package lk.ijse.etechbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_images", indexes = {
        @Index(name = "idx_product_images_product", columnList = "product_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_images_product"))
    private Product product;

    @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;
}

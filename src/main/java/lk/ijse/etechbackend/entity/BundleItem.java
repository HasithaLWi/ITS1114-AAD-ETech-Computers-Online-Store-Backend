package lk.ijse.etechbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bundle_items", indexes = {
        @Index(name = "idx_bundle_items_bundle", columnList = "bundle_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BundleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bundle_id", nullable = false, foreignKey = @ForeignKey(name = "fk_bundle_items_bundle"))
    private DealBundle bundle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_bundle_items_product"))
    private Product product;

    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;
}

package lk.ijse.etechbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "features", indexes = {
        @Index(name = "idx_features_product", columnList = "product_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Features {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String featureName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_features_product"))
    private Product product;
}

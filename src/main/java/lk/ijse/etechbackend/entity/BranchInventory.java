package lk.ijse.etechbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "branch_inventory",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_product_branch", columnNames = {"product_id", "branch_id"})
        },
        indexes = {
                @Index(name = "idx_inventory_lookup", columnList = "product_id, branch_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_inventory_product"))
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id", nullable = false, foreignKey = @ForeignKey(name = "fk_inventory_branch"))
    private Branch branch;

    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

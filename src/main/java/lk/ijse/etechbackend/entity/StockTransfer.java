package lk.ijse.etechbackend.entity;

import jakarta.persistence.*;
import lk.ijse.etechbackend.enumiration.StockTransferStatus;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transfers", indexes = {
        @Index(name = "idx_transfers_status", columnList = "status"),
        @Index(name = "idx_transfers_branches", columnList = "from_branch_id, to_branch_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransfer {

    @Id
    @Column(name = "id", length = 50)
    private String id; // e.g. "TRF-2026-001"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transfers_product"))
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_branch_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transfers_from_branch"))
    private Branch fromBranch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_branch_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transfers_to_branch"))
    private Branch toBranch;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private StockTransferStatus status = StockTransferStatus.PENDING;

    @Column(name = "reason", length = 255, nullable = false)
    private String reason;

    @Column(name = "initiated_by", length = 100, nullable = false)
    private String initiatedBy;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "dispatched_at")
    private LocalDateTime dispatchedAt;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
}

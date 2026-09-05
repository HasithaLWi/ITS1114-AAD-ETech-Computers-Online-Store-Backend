package lk.ijse.etechbackend.dto.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.ijse.etechbackend.enumiration.StockTransferStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockTransferResponseDTO {
    private String id;
    private Long productId;
    private String productName;
    private String productSku;
    private String fromBranchId;
    private String fromBranchName;
    private String toBranchId;
    private String toBranchName;
    private Integer quantity;
    private StockTransferStatus status;
    private String reason;
    private String initiatedBy;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime dispatchedAt;
    private LocalDateTime receivedAt;
    private LocalDateTime cancelledAt;
}

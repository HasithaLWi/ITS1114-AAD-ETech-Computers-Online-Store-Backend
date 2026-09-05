package lk.ijse.etechbackend.dto.transfer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransferRequestDTO {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotBlank(message = "From Branch ID is required")
    private String fromBranchId;

    @NotBlank(message = "To Branch ID is required")
    private String toBranchId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotBlank(message = "Reason is required")
    private String reason;

    private String notes;
}

package lk.ijse.etechbackend.dto.inventory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAdjustRequestDTO {
    @NotBlank(message = "Branch ID is required")
    private String branchId;

    @NotNull(message = "Quantity delta is required")
    private Integer quantityDelta;
}

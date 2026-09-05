package lk.ijse.etechbackend.dto.promotion;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BundleItemRequestDTO {
    @NotNull(message = "Product ID is required")
    private Long productId;

    private Integer quantity;
    private Integer displayOrder;
}

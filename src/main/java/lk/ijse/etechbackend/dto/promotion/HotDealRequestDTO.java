package lk.ijse.etechbackend.dto.promotion;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotDealRequestDTO {
    @NotNull(message = "Product ID is required")
    private Long productId;

    private String badge;
    private BigDecimal promoPrice;
    private BigDecimal originalPrice;
    private Integer discountPercent;
    private Integer durationSeconds;
    private Boolean isActive;
}

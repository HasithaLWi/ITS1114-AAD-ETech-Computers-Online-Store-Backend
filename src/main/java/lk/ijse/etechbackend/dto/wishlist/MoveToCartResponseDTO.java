package lk.ijse.etechbackend.dto.wishlist;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveToCartResponseDTO {
    private boolean success;
    private int movedCount;
    private BigDecimal cartTotal;
}

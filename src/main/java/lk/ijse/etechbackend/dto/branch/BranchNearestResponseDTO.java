package lk.ijse.etechbackend.dto.branch;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchNearestResponseDTO {
    private BranchDTO branch;
    private BigDecimal distanceKm;
    private BigDecimal shippingFee;
}

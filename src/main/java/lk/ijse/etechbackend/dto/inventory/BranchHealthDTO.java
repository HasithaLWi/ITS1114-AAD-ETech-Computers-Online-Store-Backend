package lk.ijse.etechbackend.dto.inventory;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchHealthDTO {
    private String branchId;
    private String branchName;
    private int totalUnits;
    private int lowStockCount;
    private int depletedCount;
    private BigDecimal healthScore;
}

package lk.ijse.etechbackend.dto.analytics;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchRevenueDTO {
    private String branchId;
    private String branchName;
    private long orderCount;
    private BigDecimal revenue;
    private BigDecimal percentage;
}

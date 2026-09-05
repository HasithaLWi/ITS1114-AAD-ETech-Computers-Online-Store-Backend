package lk.ijse.etechbackend.dto.inventory;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockHealthReportDTO {
    private long totalMonitored;
    private long depletedCount;
    private long lowStockCount;
    private Map<String, BranchHealthDTO> branchHealth;
    private List<StockAlertDTO> alerts;
}

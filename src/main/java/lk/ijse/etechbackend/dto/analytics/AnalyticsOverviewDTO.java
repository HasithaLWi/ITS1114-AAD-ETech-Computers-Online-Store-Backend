package lk.ijse.etechbackend.dto.analytics;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsOverviewDTO {
    private BigDecimal grossRevenue;
    private long totalOrders;
    private BigDecimal avgOrderValue;
    private long activeUsers;
}

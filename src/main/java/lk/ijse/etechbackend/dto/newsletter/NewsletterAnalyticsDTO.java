package lk.ijse.etechbackend.dto.newsletter;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsletterAnalyticsDTO {
    private long totalSubscribers;
    private long activeSubscribers;
    private long unsubscribedCount;
    private BigDecimal activeRate;
    private long totalCampaigns;
    private BigDecimal avgOpenRate;
    private BigDecimal avgClickRate;
    private Map<String, Long> channelDistribution;
}

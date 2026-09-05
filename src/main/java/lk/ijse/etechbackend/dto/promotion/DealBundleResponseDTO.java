package lk.ijse.etechbackend.dto.promotion;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DealBundleResponseDTO {
    private Long id;
    private String badge;
    private String eyebrow;
    private String title;
    private String subtitle;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal savingAmount;
    private Integer savingPercent;
    private Integer targetQuota;
    private Integer soldCount;
    private Integer stockLeft;
    private Integer claimedPercent;
    private Integer durationSeconds;
    private LocalDateTime timerUpdatedAt;
    private Boolean isActive;
    private List<BundleItemDTO> componentsBreakdown;
    private LocalDateTime createdAt;
}

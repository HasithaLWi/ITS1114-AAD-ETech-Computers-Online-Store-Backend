package lk.ijse.etechbackend.dto.promotion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealBundleRequestDTO {
    private String badge;
    private String eyebrow;

    @NotBlank(message = "Title is required")
    private String title;

    private String subtitle;
    private String imageUrl;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    private BigDecimal originalPrice;
    private Integer targetQuota;
    private Integer soldCount;
    private Integer durationSeconds;
    private Boolean isActive;
    private List<BundleItemRequestDTO> bundleItems;
}

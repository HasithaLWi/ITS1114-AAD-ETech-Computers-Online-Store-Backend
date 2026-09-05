package lk.ijse.etechbackend.dto.promotion;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.ijse.etechbackend.dto.productsdto.ProductResponseDTO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HotDealResponseDTO {
    private Long id;
    private Long productId;
    private ProductResponseDTO product;
    private String badge;
    private BigDecimal promoPrice;
    private BigDecimal originalPrice;
    private Integer discountPercent;
    private Integer durationSeconds;
    private LocalDateTime timerUpdatedAt;
    private Boolean isActive;
    private LocalDateTime createdAt;
}

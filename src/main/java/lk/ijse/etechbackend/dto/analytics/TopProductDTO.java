package lk.ijse.etechbackend.dto.analytics;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopProductDTO {
    private Long productId;
    private String name;
    private long unitsSold;
    private BigDecimal revenue;
}

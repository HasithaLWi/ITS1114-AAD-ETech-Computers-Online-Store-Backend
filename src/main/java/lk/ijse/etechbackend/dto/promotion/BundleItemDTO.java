package lk.ijse.etechbackend.dto.promotion;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BundleItemDTO {
    private Long id;
    private Long productId;
    private String name;
    private String sku;
    private Integer qty;
    private BigDecimal unitPrice;
    private String image;
    private Map<String, String> specs;
    private Integer displayOrder;
}

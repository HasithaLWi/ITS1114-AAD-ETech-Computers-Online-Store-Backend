package lk.ijse.etechbackend.dto.inventory;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockAlertDTO {
    private Long productId;
    private String productName;
    private String productSku;
    private String category;
    private String alertType; // "DEPLETED", "LOW_STOCK"
    private Integer totalStock;
    private Integer lowStockMargin;
    private Map<String, Integer> branchStock;
}

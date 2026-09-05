package lk.ijse.etechbackend.dto.inventory;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventorySettingsDTO {
    private Boolean alertEnabled;
    private Integer lowStockMargin;
}

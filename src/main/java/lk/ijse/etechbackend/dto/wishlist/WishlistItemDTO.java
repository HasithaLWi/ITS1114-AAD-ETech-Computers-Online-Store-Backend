package lk.ijse.etechbackend.dto.wishlist;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WishlistItemDTO {
    private Long id;
    private Long productId;
    private String name;
    private String sku;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String image;
    private String category;
    private Boolean inStock;
    private Integer totalStock;
    private LocalDateTime savedAt;
}

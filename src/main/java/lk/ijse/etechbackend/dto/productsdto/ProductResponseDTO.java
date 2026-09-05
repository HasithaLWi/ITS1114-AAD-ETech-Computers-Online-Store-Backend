package lk.ijse.etechbackend.dto.productsdto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.ijse.etechbackend.enumiration.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String categoryId;
    private String brandId;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal rating;
    private Integer reviewsCount;
    private String description;
    private String fullDescription;
    private String sku;
    private String badgeId;
    private String warranty;
    private Boolean alertEnabled;
    private Integer lowStockMargin;
    private Map<String, String> specs;
    private List<String> features;
    private List<String> images;
    private Map<String, Integer> branchStock;
    private Integer totalStock;
    private Status productStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}


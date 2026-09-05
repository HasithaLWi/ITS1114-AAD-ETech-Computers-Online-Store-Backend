package lk.ijse.etechbackend.dto.categorydto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.ijse.etechbackend.enumiration.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponseDTO {

    private String id; // e.g. "cat-laptops", "cat-components"

    private String superCategoryId; // e.g. "cat-electronics", "cat-hardware"

    private String name;

    private String slug;

    private String icon = "📦";

    private String description;

    private Boolean featured = false;

    private Integer displayOrder = 0;

    private Status categoryStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

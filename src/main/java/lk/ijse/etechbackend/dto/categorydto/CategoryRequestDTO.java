package lk.ijse.etechbackend.dto.categorydto;

import jakarta.validation.constraints.NotBlank;
import lk.ijse.etechbackend.enumiration.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDTO {

    private String id; // e.g. "cat-laptops", "cat-components"
    @NotBlank(message = "Super category ID is required if this is a subcategory or leave it null if this is a main category")
    private String superCategoryId;
    private String name;
    private String slug;
    private String icon = "📦";
    private String description;
    private Boolean featured = false;
    private Integer displayOrder = 0;
    private Status categoryStatus;

}

package lk.ijse.etechbackend.dto.branddto;

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
public class BrandRequestDTO {

    private String id; // e.g. "brd-asus", "brd-msi"

    @NotBlank(message = "Brand name is required")
    private String name;

    private String slug;

    private String logoUrl;

    @Builder.Default
    private String country = "Global";

    private String foundedYear;

    private String websiteUrl;

    private String tagline;

    private String description;

    @Builder.Default
    private Boolean featured = false;

    private Status status;

    @Builder.Default
    private Integer displayOrder = 0;
}

package lk.ijse.etechbackend.dto.branddto;

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
public class BrandResponseDTO {

    private String id; // e.g. "brd-asus", "brd-msi"

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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

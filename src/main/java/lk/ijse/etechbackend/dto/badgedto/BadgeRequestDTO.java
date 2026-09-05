package lk.ijse.etechbackend.dto.badgedto;

import jakarta.validation.constraints.NotBlank;
import lk.ijse.etechbackend.enumiration.BadgeRuleType;
import lk.ijse.etechbackend.enumiration.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BadgeRequestDTO {

    private String id; // e.g. "bdg-bestseller", "bdg-hotdeal"

    @NotBlank(message = "Badge name is required")
    private String name;

    private String slug;

    @Builder.Default
    private String colorKey = "blue";

    @Builder.Default
    private String colorHex = "#2563eb";

    private String purpose;

    private String standardDescription;

    @Builder.Default
    private BadgeRuleType ruleType = BadgeRuleType.manual;

    @Builder.Default
    private String criteria = "custom";

    @Builder.Default
    private Integer priority = 10;

    @Builder.Default
    private Boolean isSystemDefault = false;

    @Builder.Default
    private Boolean canEdit = true;

    @Builder.Default
    private Boolean canDelete = true;

    private Status status;

}

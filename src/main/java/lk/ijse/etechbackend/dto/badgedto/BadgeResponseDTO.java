package lk.ijse.etechbackend.dto.badgedto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.ijse.etechbackend.enumiration.BadgeRuleType;
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
public class BadgeResponseDTO {

    private String id; // e.g. "bdg-bestseller", "bdg-hotdeal"

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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

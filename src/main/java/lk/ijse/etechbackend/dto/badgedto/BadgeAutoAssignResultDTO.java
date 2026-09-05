package lk.ijse.etechbackend.dto.badgedto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeAutoAssignResultDTO {
    private int evaluatedCount;
    private int assignedCount;
    private List<BadgeChangeDTO> changes;
}

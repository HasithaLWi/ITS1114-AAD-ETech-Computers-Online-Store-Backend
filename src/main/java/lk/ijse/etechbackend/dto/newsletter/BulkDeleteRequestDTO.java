package lk.ijse.etechbackend.dto.newsletter;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkDeleteRequestDTO {
    @NotEmpty(message = "Subscriber IDs cannot be empty")
    private List<Long> ids;
}

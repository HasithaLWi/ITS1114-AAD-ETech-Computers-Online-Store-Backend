package lk.ijse.etechbackend.dto.newsletter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lk.ijse.etechbackend.enumiration.SubscriberStatus;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkStatusRequestDTO {
    @NotEmpty(message = "Subscriber IDs cannot be empty")
    private List<Long> ids;

    @NotNull(message = "Status is required")
    private SubscriberStatus status;
}

package lk.ijse.etechbackend.dto.transfer;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferMetricsDTO {
    private long pendingCount;
    private long inTransitCount;
    private long receivedCount;
    private long totalUnitsMoved;
}

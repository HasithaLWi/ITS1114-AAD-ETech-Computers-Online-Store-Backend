package lk.ijse.etechbackend.dto.transfer;

import jakarta.validation.constraints.NotNull;
import lk.ijse.etechbackend.enumiration.StockTransferStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferStatusUpdateDTO {
    @NotNull(message = "Status is required")
    private StockTransferStatus status;
}

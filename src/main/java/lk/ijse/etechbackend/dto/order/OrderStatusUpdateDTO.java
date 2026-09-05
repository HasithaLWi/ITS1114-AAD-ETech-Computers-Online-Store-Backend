package lk.ijse.etechbackend.dto.order;

import jakarta.validation.constraints.NotNull;
import lk.ijse.etechbackend.enumiration.OrderStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateDTO {
    @NotNull(message = "Status is required")
    private OrderStatus status;
}

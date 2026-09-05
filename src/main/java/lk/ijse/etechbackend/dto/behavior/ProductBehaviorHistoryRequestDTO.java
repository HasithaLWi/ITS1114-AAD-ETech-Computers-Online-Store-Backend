package lk.ijse.etechbackend.dto.behavior;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lk.ijse.etechbackend.enumiration.ProductBehaviorActor;
import lk.ijse.etechbackend.enumiration.ProductBehaviorEventType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBehaviorHistoryRequestDTO {
    @NotNull(message = "Product ID is required")
    private Long productId;

    private String productName;

    @NotNull(message = "Event type is required")
    private ProductBehaviorEventType eventType;

    private String previousValue;
    private String newValue;

    @NotBlank(message = "Trigger reason is required")
    private String triggerReason;

    private Object metricsSnapshot;
    private ProductBehaviorActor actor;
}

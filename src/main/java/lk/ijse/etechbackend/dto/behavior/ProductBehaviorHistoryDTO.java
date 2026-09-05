package lk.ijse.etechbackend.dto.behavior;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.ijse.etechbackend.enumiration.ProductBehaviorActor;
import lk.ijse.etechbackend.enumiration.ProductBehaviorEventType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductBehaviorHistoryDTO {
    private String id;
    private Long productId;
    private String productName;
    private ProductBehaviorEventType eventType;
    private String previousValue;
    private String newValue;
    private String triggerReason;
    private Object metricsSnapshot;
    private ProductBehaviorActor actor;
    private LocalDateTime createdAt;
}

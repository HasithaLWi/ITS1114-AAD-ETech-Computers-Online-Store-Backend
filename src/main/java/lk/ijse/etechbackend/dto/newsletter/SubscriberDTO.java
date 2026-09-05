package lk.ijse.etechbackend.dto.newsletter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.ijse.etechbackend.enumiration.SubscriberSource;
import lk.ijse.etechbackend.enumiration.SubscriberStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriberDTO {
    private Long id;
    private String email;
    private String name;
    private SubscriberStatus status;
    private SubscriberSource source;
    private List<String> tags;
    private LocalDateTime subscribedAt;
    private LocalDateTime unsubscribedAt;
    private LocalDateTime lastCampaignSentAt;
}

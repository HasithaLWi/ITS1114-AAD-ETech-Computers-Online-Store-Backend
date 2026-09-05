package lk.ijse.etechbackend.dto.newsletter;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignSendRequestDTO {
    @NotBlank(message = "Subject is required")
    private String subject;

    private String preheader;
    private String category;
    private String targetSegment;
    private String contentHtml;
    private String authorName;
}

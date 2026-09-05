package lk.ijse.etechbackend.dto.newsletter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignDTO {
    private String id;
    private String subject;
    private String preheader;
    private String category;
    private String targetSegment;
    private String contentHtml;
    private LocalDateTime sentAt;
    private Integer recipientsCount;
    private String status;
    private BigDecimal openRate;
    private BigDecimal clickRate;
    private String authorName;
}

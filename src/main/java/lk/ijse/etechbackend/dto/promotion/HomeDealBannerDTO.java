package lk.ijse.etechbackend.dto.promotion;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HomeDealBannerDTO {
    private Integer id;
    private String dealTag;
    private String heading;
    private String subtitle;
    private String buttonText;
    private String buttonUrl;
    private String backgroundImage;
    private Integer durationSeconds;
    private LocalDateTime timerUpdatedAt;
    private Boolean isActive;
}

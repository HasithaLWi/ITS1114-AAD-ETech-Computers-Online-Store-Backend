package lk.ijse.etechbackend.dto.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessProfileDTO {
    private Integer id;
    private String storeName;
    private String tagline;
    private String registrationNo;
    private String taxId;
    private String isoCert;
    private String supportEmail;
    private String hotline;
    private String headquarters;
    private String workingHours;
    private String missionStatement;
    private String companyStory;
    private LocalDateTime updatedAt;
}

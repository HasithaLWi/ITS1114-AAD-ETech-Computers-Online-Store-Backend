package lk.ijse.etechbackend.dto.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewResponseDTO {
    private String id;
    private Long productId;
    private Long userId;
    private String userName;
    private String userEmail;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}

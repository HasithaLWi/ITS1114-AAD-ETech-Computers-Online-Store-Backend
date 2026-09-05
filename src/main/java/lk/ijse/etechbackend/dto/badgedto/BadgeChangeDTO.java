package lk.ijse.etechbackend.dto.badgedto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeChangeDTO {
    private Long productId;
    private String productName;
    private String oldBadge;
    private String newBadge;
    private String reason;
}

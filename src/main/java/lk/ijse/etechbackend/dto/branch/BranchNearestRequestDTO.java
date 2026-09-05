package lk.ijse.etechbackend.dto.branch;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchNearestRequestDTO {
    private String city;
    private BigDecimal latitude;
    private BigDecimal longitude;
}

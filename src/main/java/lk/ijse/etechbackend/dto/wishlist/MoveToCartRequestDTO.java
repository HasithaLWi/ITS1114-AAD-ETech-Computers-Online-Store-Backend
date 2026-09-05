package lk.ijse.etechbackend.dto.wishlist;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveToCartRequestDTO {
    private List<Long> productIds;
}

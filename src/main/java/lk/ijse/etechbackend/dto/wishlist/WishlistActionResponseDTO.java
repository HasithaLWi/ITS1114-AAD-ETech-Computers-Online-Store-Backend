package lk.ijse.etechbackend.dto.wishlist;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WishlistActionResponseDTO {
    private boolean success;
    private Boolean added;
    private Long productId;
    private String message;
    private long wishlistCount;
}

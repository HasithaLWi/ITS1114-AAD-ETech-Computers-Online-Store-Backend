package lk.ijse.etechbackend.dto.wishlist;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponseDTO {
    private boolean success;
    private int total;
    private List<WishlistItemDTO> items;
}

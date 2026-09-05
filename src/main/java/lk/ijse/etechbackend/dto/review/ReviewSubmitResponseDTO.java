package lk.ijse.etechbackend.dto.review;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSubmitResponseDTO {
    private ReviewResponseDTO review;
    private BigDecimal updatedProductRating;
    private Integer totalReviews;
}

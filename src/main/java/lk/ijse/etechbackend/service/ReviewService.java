package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.review.ReviewRequestDTO;
import lk.ijse.etechbackend.dto.review.ReviewResponseDTO;
import lk.ijse.etechbackend.dto.review.ReviewSubmitResponseDTO;

import java.util.List;

public interface ReviewService {
    List<ReviewResponseDTO> getReviewsByProduct(Long productId, int page, int size);
    ReviewSubmitResponseDTO submitReview(String username, Long productId, ReviewRequestDTO request);
    void deleteReview(String username, String reviewId);
}

package lk.ijse.etechbackend.controller;

import jakarta.validation.Valid;
import lk.ijse.etechbackend.dto.CommonResponse;
import lk.ijse.etechbackend.dto.review.ReviewRequestDTO;
import lk.ijse.etechbackend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping(value = "/api/v1/products/{productId}/reviews", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        log.info("REST: Fetching reviews for product ID: {}", productId);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Product reviews retrieved successfully")
                .body(reviewService.getReviewsByProduct(productId, page, size))
                .build());
    }

    @PostMapping(value = "/api/v1/products/{productId}/reviews", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResponse> submitReview(@PathVariable Long productId,
                                                       @Valid @RequestBody ReviewRequestDTO request,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        log.info("REST: Submitting review for product ID: {} by user: {}", productId, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Review submitted successfully")
                .body(reviewService.submitReview(username, productId, request))
                .build());
    }

    @DeleteMapping(value = "/api/v1/reviews/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> deleteReview(@PathVariable String id,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        log.info("REST: Deleting review ID: {} by user: {}", id, username);
        reviewService.deleteReview(username, id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Review deleted successfully")
                .build());
    }
}

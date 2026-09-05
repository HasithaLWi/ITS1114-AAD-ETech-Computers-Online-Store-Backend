package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.review.ReviewRequestDTO;
import lk.ijse.etechbackend.dto.review.ReviewResponseDTO;
import lk.ijse.etechbackend.dto.review.ReviewSubmitResponseDTO;
import lk.ijse.etechbackend.entity.Product;
import lk.ijse.etechbackend.entity.ProductReview;
import lk.ijse.etechbackend.entity.User;
import lk.ijse.etechbackend.enumiration.UserRole;
import lk.ijse.etechbackend.exception.ForbiddenException;
import lk.ijse.etechbackend.exception.ResourceNotFoundException;
import lk.ijse.etechbackend.repository.ProductRepository;
import lk.ijse.etechbackend.repository.ProductReviewRepository;
import lk.ijse.etechbackend.repository.UserRepository;
import lk.ijse.etechbackend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByProduct(Long productId, int page, int size) {
        log.info("Fetching reviews for product ID: {} (page={}, size={})", productId, page, size);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProductReview> reviewPage = reviewRepository.findByProductId(productId, pageRequest);

        return reviewPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewSubmitResponseDTO submitReview(String username, Long productId, ReviewRequestDTO request) {
        log.info("Submitting review for product ID {} by user {}", productId, username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        Optional<ProductReview> existingOpt = reviewRepository.findByProductIdAndUserId(productId, user.getId());
        ProductReview review;
        if (existingOpt.isPresent()) {
            review = existingOpt.get();
            review.setRating(request.getRating());
            review.setComment(request.getComment());
            review.setUserName(user.getName());
            review.setUserEmail(user.getEmail());
        } else {
            String reviewId = "REV-" + System.currentTimeMillis() % 1000000;
            review = ProductReview.builder()
                    .id(reviewId)
                    .product(product)
                    .user(user)
                    .userName(user.getName())
                    .userEmail(user.getEmail())
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .build();
        }

        ProductReview saved = reviewRepository.save(review);

        // Recalculate average rating & review count for product
        Double avgRating = reviewRepository.calculateAverageRatingForProduct(productId);
        long count = reviewRepository.countByProductId(productId);

        BigDecimal roundedRating = avgRating != null
                ? BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(5.0);

        product.setRating(roundedRating);
        product.setReviewsCount((int) count);
        productRepository.save(product);

        log.info("Product ID {} rating updated to {} with {} reviews", productId, roundedRating, count);

        return ReviewSubmitResponseDTO.builder()
                .review(toDTO(saved))
                .updatedProductRating(roundedRating)
                .totalReviews((int) count)
                .build();
    }

    @Override
    public void deleteReview(String username, String reviewId) {
        log.info("Deleting review ID {} by user {}", reviewId, username);
        ProductReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        boolean isOwner = review.getUser() != null && review.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == UserRole.SUPERADMIN || currentUser.getRole() == UserRole.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("You do not have permission to delete this review");
        }

        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);

        // Recalculate
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            Double avgRating = reviewRepository.calculateAverageRatingForProduct(productId);
            long count = reviewRepository.countByProductId(productId);
            BigDecimal roundedRating = avgRating != null
                    ? BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP)
                    : BigDecimal.valueOf(5.0);
            product.setRating(roundedRating);
            product.setReviewsCount((int) count);
            productRepository.save(product);
        }
    }

    private ReviewResponseDTO toDTO(ProductReview r) {
        return ReviewResponseDTO.builder()
                .id(r.getId())
                .productId(r.getProduct() != null ? r.getProduct().getId() : null)
                .userId(r.getUser() != null ? r.getUser().getId() : null)
                .userName(r.getUserName())
                .userEmail(r.getUserEmail())
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build();
    }
}

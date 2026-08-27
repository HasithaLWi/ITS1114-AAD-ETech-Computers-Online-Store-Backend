package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, String> {

    Page<ProductReview> findByProductId(Long productId, Pageable pageable);

    List<ProductReview> findByProductIdOrderByCreatedAtDesc(Long productId);

    Optional<ProductReview> findByProductIdAndUserId(Long productId, Long userId);

    boolean existsByProductIdAndUserId(Long productId, Long userId);

    @Query("SELECT AVG(pr.rating) FROM ProductReview pr WHERE pr.product.id = :productId")
    Double calculateAverageRatingForProduct(@Param("productId") Long productId);

    long countByProductId(Long productId);
}

package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.ProductBehaviorHistory;
import lk.ijse.etechbackend.enumiration.ProductBehaviorEventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductBehaviorHistoryRepository extends JpaRepository<ProductBehaviorHistory, String> {

    Page<ProductBehaviorHistory> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<ProductBehaviorHistory> findByEventTypeOrderByCreatedAtDesc(ProductBehaviorEventType eventType, Pageable pageable);

    List<ProductBehaviorHistory> findByProductIdOrderByCreatedAtDesc(Long productId);
}

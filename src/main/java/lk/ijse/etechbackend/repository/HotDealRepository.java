package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.HotDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotDealRepository extends JpaRepository<HotDeal, Long> {

    Optional<HotDeal> findByProductId(Long productId);

    List<HotDeal> findByIsActiveTrue();

    boolean existsByProductId(Long productId);
}

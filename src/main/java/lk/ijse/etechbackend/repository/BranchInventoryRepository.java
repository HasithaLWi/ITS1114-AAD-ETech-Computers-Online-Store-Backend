package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.BranchInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchInventoryRepository extends JpaRepository<BranchInventory, Long> {

    List<BranchInventory> findByProductId(Long productId);

    List<BranchInventory> findByBranchId(String branchId);

    Optional<BranchInventory> findByProductIdAndBranchId(Long productId, String branchId);

    @Query("SELECT SUM(bi.quantity) FROM BranchInventory bi WHERE bi.product.id = :productId")
    Integer calculateTotalStockForProduct(@Param("productId") Long productId);

    void deleteByProductId(Long productId);
}

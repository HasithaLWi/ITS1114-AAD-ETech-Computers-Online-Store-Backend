package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.StockTransfer;
import lk.ijse.etechbackend.enumiration.StockTransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransfer, String> {

    List<StockTransfer> findByStatus(StockTransferStatus status);

    List<StockTransfer> findByFromBranchIdOrToBranchId(String fromBranchId, String toBranchId);

    List<StockTransfer> findByProductId(Long productId);

    long countByStatus(StockTransferStatus status);

    @Query("SELECT COALESCE(SUM(st.quantity), 0) FROM StockTransfer st WHERE st.status = 'RECEIVED'")
    Long calculateTotalUnitsMoved();
}

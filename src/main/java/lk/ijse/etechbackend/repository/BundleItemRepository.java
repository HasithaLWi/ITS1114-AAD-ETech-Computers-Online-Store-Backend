package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.BundleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BundleItemRepository extends JpaRepository<BundleItem, Long> {

    List<BundleItem> findByBundleIdOrderByDisplayOrderAsc(Long bundleId);

    void deleteByBundleId(Long bundleId);
}

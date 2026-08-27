package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.DealBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealBundleRepository extends JpaRepository<DealBundle, Long> {

    List<DealBundle> findByIsActiveTrue();
}

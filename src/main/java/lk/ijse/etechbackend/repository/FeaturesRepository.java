package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.Features;
import lk.ijse.etechbackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeaturesRepository extends JpaRepository<Features, Long> {
    @Query("SELECT f.featureName FROM Features f WHERE f.product = :product")
    List<String> getAllFeatureNamesByProduct(Product product);

    List<Features> findByProduct(Product product);
}

package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    List<Product> findByCategorySlug(String categorySlug);

    List<Product> findByBrandIgnoreCase(String brand);

    List<Product> findByBadgeIgnoreCase(String badge);

    long countByCategorySlug(String categorySlug);

    long countByBrandIgnoreCase(String brand);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images LEFT JOIN FETCH p.branchInventories bi LEFT JOIN FETCH bi.branch WHERE p.id = :id")
    Optional<Product> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images LEFT JOIN FETCH p.branchInventories bi LEFT JOIN FETCH bi.branch WHERE p.sku = :sku")
    Optional<Product> findBySkuWithDetails(@Param("sku") String sku);
}

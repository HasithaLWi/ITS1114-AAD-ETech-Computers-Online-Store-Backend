package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.Category;
import lk.ijse.etechbackend.entity.Product;
import lk.ijse.etechbackend.enumiration.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.category c " +
            "LEFT JOIN c.superCategory sc " +
            "LEFT JOIN sc.superCategory ssc " +
            "WHERE (:category IS NULL OR c.id = :category OR sc.id = :category OR ssc.id = :category) " +
            "AND (:brand IS NULL OR p.brand = :brand) " +
            "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:badge IS NULL OR p.badge = :badge) " +
            "AND (p.productStatus = 'ACTIVE')")
    Page<Product> findProductsWithOptionalFilter(@Param("category") String category,
                                                   @Param("brand") String brand,
                                                   @Param("search") String search,
                                                   @Param("minPrice") BigDecimal minPrice,
                                                   @Param("maxPrice") BigDecimal maxPrice,
                                                   @Param("badge") String badge,
                                                   Pageable pageable);
    @Query("SELECT p FROM Product  p WHERE p.productStatus != 'DELETED'")
    List<Product> findAllProducts();

    Optional<Product> findBySku(String sku);

    List<Product> findByCategory(Category category);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    List<Product> findAllByProductStatus(Status productStatus);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images LEFT JOIN FETCH p.branchInventories bi LEFT JOIN FETCH bi.branch WHERE p.id = :id")
    Optional<Product> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images LEFT JOIN FETCH p.branchInventories bi LEFT JOIN FETCH bi.branch WHERE p.sku = :sku")
    Optional<Product> findBySkuWithDetails(@Param("sku") String sku);
}

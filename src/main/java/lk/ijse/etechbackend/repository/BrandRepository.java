package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.Brand;
import lk.ijse.etechbackend.enumiration.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String> {

    Optional<Brand> findBySlug(String slug);

    Optional<Brand> findByName(String name);

    Optional<Brand> findByNameIgnoreCase(String name);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlugAndIdNot(String slug, String id);

    boolean existsByNameIgnoreCaseAndIdNot(String name, String id);


    @Query("SELECT b FROM Brand b WHERE b.featured = true AND b.status != 'DELETED' ORDER BY b.displayOrder ASC")
    List<Brand> findByFeaturedTrueOrderByDisplayOrderAsc();

    List<Brand> findAllByStatus(Status status);

    @Query("SELECT b FROM Brand b WHERE b.status != 'DELETED' ORDER BY b.displayOrder ASC")
    List<Brand> findAllByOrderByDisplayOrderAsc();

    @Query("SELECT b FROM Brand b WHERE b.status != 'DELETED' AND (LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.slug) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.country) LIKE LOWER(CONCAT('%', :search, '%'))) ORDER BY b.displayOrder ASC")
    List<Brand> findAllBySearch(@Param("search") String search);
}

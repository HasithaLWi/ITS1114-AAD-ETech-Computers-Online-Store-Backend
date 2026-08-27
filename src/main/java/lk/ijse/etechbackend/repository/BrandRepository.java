package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String> {

    Optional<Brand> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByNameIgnoreCase(String name);

    List<Brand> findByActiveTrueOrderByDisplayOrderAsc();

    List<Brand> findByFeaturedTrueAndActiveTrueOrderByDisplayOrderAsc();

    List<Brand> findAllByOrderByDisplayOrderAsc();
}

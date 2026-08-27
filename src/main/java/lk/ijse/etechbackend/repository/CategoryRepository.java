package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Category> findByFeaturedTrueOrderByDisplayOrderAsc();

    List<Category> findAllByOrderByDisplayOrderAsc();
}

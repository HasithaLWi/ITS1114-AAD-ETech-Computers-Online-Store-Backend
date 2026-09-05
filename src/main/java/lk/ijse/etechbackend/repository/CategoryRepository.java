package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.dto.categorydto.CategoryResponseDTO;
import lk.ijse.etechbackend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    @Query("SELECT new lk.ijse.etechbackend.dto.categorydto.CategoryResponseDTO(c.id, c.superCategory.id, c.name, c.slug, c.icon, c.description, c.featured, c.displayOrder, c.categoryStatus, c.createdAt, c.updatedAt) " +
            "FROM Category c WHERE c.categoryStatus != 'DELETED' ORDER BY c.displayOrder ASC")
    List<CategoryResponseDTO> findAllCategory();

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT new lk.ijse.etechbackend.dto.categorydto.CategoryResponseDTO(c.id, c.superCategory.id, c.name, c.slug, c.icon, c.description, c.featured, c.displayOrder, c.categoryStatus, c.createdAt, c.updatedAt) " +
            "FROM Category c WHERE c.categoryStatus != 'DELETED' AND (c.name LIKE %:search% OR c.slug LIKE %:search%) ORDER BY c.displayOrder ASC")
    List<CategoryResponseDTO> findAllBySearch(@Param("search") String search);

    @Query("SELECT c FROM Category c WHERE c.superCategory = :superCategory")
    List<Category> findCategoryBySuperCategory(@Param("superCategory") Category superCategory);

    List<Category> findByFeaturedTrueOrderByDisplayOrderAsc();

    List<Category> findAllByOrderByDisplayOrderAsc();
}

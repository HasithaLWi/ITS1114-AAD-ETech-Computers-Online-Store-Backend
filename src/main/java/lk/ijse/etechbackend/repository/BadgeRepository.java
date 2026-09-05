package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.Badge;
import lk.ijse.etechbackend.enumiration.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, String> {

    Optional<Badge> findBySlug(String slug);

    Optional<Badge> findByName(String name);

    Optional<Badge> findByNameIgnoreCase(String name);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlugAndIdNot(String slug, String id);

    boolean existsByNameIgnoreCaseAndIdNot(String name, String id);

    List<Badge> findAllByStatus(Status status);

    @Query("SELECT b FROM Badge b WHERE b.status = 'ACTIVE' ORDER BY b.priority ASC")
    List<Badge> findByActiveOrderByPriorityAsc();

    @Query("SELECT b FROM Badge b WHERE b.status != 'DELETED' ORDER BY b.priority ASC")
    List<Badge> findAllByOrderByPriorityAsc();

    @Query("SELECT b FROM Badge b WHERE b.status != 'DELETED' AND (LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.slug) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.purpose) LIKE LOWER(CONCAT('%', :search, '%'))) ORDER BY b.priority ASC")
    List<Badge> findAllBySearch(@Param("search") String search);
}

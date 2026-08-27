package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, String> {

    Optional<Badge> findBySlug(String slug);

    Optional<Badge> findByNameIgnoreCase(String name);

    boolean existsBySlug(String slug);

    List<Badge> findByIsActiveTrueOrderByPriorityAsc();
}

package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.NewsletterSubscriber;
import lk.ijse.etechbackend.enumiration.SubscriberSource;
import lk.ijse.etechbackend.enumiration.SubscriberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsletterSubscriberRepository extends JpaRepository<NewsletterSubscriber, Long> {

    Optional<NewsletterSubscriber> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<NewsletterSubscriber> findByStatus(SubscriberStatus status);

    long countByStatus(SubscriberStatus status);

    @Query("SELECT s FROM NewsletterSubscriber s WHERE " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:source IS NULL OR s.source = :source) AND " +
            "(:search IS NULL OR LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(COALESCE(s.name, '')) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<NewsletterSubscriber> filterSubscribers(
            @Param("search") String search,
            @Param("status") SubscriberStatus status,
            @Param("source") SubscriberSource source,
            Pageable pageable);
}

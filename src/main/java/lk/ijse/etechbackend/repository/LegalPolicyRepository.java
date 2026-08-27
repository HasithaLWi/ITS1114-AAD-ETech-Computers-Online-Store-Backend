package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.LegalPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LegalPolicyRepository extends JpaRepository<LegalPolicy, String> {

    Optional<LegalPolicy> findById(String id);
}

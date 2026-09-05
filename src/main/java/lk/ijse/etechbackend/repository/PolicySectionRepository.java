package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.PolicySections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicySectionRepository extends JpaRepository<PolicySections, String> {
}

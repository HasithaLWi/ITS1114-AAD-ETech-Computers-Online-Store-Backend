package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.BusinessProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, Integer> {
}

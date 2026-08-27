package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.HomeDealBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeDealBannerRepository extends JpaRepository<HomeDealBanner, Integer> {
}

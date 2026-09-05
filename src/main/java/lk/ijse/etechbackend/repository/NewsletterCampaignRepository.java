package lk.ijse.etechbackend.repository;

import lk.ijse.etechbackend.entity.NewsletterCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface NewsletterCampaignRepository extends JpaRepository<NewsletterCampaign, String> {

    List<NewsletterCampaign> findAllByOrderBySentAtDesc();

    @Query("SELECT COALESCE(AVG(c.openRate), 0) FROM NewsletterCampaign c")
    BigDecimal calculateAverageOpenRate();

    @Query("SELECT COALESCE(AVG(c.clickRate), 0) FROM NewsletterCampaign c")
    BigDecimal calculateAverageClickRate();
}

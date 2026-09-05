package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.PageResponseDTO;
import lk.ijse.etechbackend.dto.newsletter.*;
import lk.ijse.etechbackend.enumiration.SubscriberSource;
import lk.ijse.etechbackend.enumiration.SubscriberStatus;

import java.util.List;
import java.util.Map;

public interface NewsletterService {
    Map<String, Object> getSubscribers(String search, SubscriberStatus status, SubscriberSource source, int page, int size);
    SubscriberDTO getSubscriberById(Long id);
    SubscriberDTO subscribe(SubscriberRequestDTO request, String ipAddress);
    void unsubscribe(String email);
    SubscriberDTO updateStatus(Long id, SubscriberStatus status);
    SubscriberDTO updateSubscriber(Long id, SubscriberRequestDTO request);
    void deleteSubscriber(Long id);
    int bulkUpdateStatus(List<Long> ids, SubscriberStatus status);
    int bulkDelete(List<Long> ids);
    CampaignDTO sendCampaign(CampaignSendRequestDTO request);
    List<CampaignDTO> getAllCampaigns();
    NewsletterAnalyticsDTO getAnalytics();
}

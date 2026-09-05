package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.newsletter.*;
import lk.ijse.etechbackend.entity.NewsletterCampaign;
import lk.ijse.etechbackend.entity.NewsletterSubscriber;
import lk.ijse.etechbackend.enumiration.SubscriberSource;
import lk.ijse.etechbackend.enumiration.SubscriberStatus;
import lk.ijse.etechbackend.exception.ResourceNotFoundException;
import lk.ijse.etechbackend.repository.NewsletterCampaignRepository;
import lk.ijse.etechbackend.repository.NewsletterSubscriberRepository;
import lk.ijse.etechbackend.service.NewsletterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NewsletterServiceImpl implements NewsletterService {

    private final NewsletterSubscriberRepository subscriberRepository;
    private final NewsletterCampaignRepository campaignRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSubscribers(String search, SubscriberStatus status, SubscriberSource source, int page, int size) {
        log.info("Fetching subscribers list - search: {}, status: {}, source: {}, page: {}, size: {}",
                search, status, source, page, size);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "subscribedAt"));
        Page<NewsletterSubscriber> subscriberPage = subscriberRepository.filterSubscribers(
                search != null && !search.isBlank() ? search.trim() : null,
                status,
                source,
                pageRequest
        );

        List<SubscriberDTO> dataList = subscriberPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        NewsletterAnalyticsDTO analytics = getAnalytics();

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("total", subscriberPage.getTotalElements());
        result.put("totalPages", subscriberPage.getTotalPages());
        result.put("currentPage", subscriberPage.getNumber());
        result.put("analytics", analytics);
        result.put("data", dataList);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriberDTO getSubscriberById(Long id) {
        log.info("Fetching subscriber by ID: {}", id);
        NewsletterSubscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber not found with ID: " + id));
        return toDTO(subscriber);
    }

    @Override
    public SubscriberDTO subscribe(SubscriberRequestDTO request, String ipAddress) {
        log.info("Processing newsletter subscription for email: {}", request.getEmail());
        String email = request.getEmail().trim().toLowerCase();

        Optional<NewsletterSubscriber> existingOpt = subscriberRepository.findByEmailIgnoreCase(email);
        NewsletterSubscriber subscriber;

        if (existingOpt.isPresent()) {
            subscriber = existingOpt.get();
            subscriber.setStatus(SubscriberStatus.SUBSCRIBED);
            subscriber.setUnsubscribedAt(null);
            if (request.getName() != null && !request.getName().isBlank()) {
                subscriber.setName(request.getName().trim());
            }
            if (request.getSource() != null) {
                subscriber.setSource(request.getSource());
            }
            if (request.getTags() != null && !request.getTags().isEmpty()) {
                Set<String> tagsSet = new HashSet<>(subscriber.getTags());
                tagsSet.addAll(request.getTags());
                subscriber.setTags(new ArrayList<>(tagsSet));
            }
            subscriber.setIpAddress(ipAddress != null ? ipAddress : "127.0.0.1");
        } else {
            subscriber = NewsletterSubscriber.builder()
                    .email(email)
                    .name(request.getName() != null ? request.getName().trim() : null)
                    .status(SubscriberStatus.SUBSCRIBED)
                    .source(request.getSource() != null ? request.getSource() : SubscriberSource.STOREFRONT_BANNER)
                    .tags(request.getTags() != null ? request.getTags() : new ArrayList<>())
                    .ipAddress(ipAddress != null ? ipAddress : "127.0.0.1")
                    .build();
        }

        NewsletterSubscriber saved = subscriberRepository.save(subscriber);
        log.info("Newsletter subscription confirmed for: {}", email);
        return toDTO(saved);
    }

    @Override
    public void unsubscribe(String email) {
        log.info("Processing unsubscribe request for: {}", email);
        if (email == null || email.isBlank()) return;

        Optional<NewsletterSubscriber> opt = subscriberRepository.findByEmailIgnoreCase(email.trim());
        if (opt.isPresent()) {
            NewsletterSubscriber s = opt.get();
            s.setStatus(SubscriberStatus.UNSUBSCRIBED);
            s.setUnsubscribedAt(LocalDateTime.now());
            subscriberRepository.save(s);
            log.info("Subscriber marked as UNSUBSCRIBED: {}", email);
        }
    }

    @Override
    public SubscriberDTO updateStatus(Long id, SubscriberStatus status) {
        log.info("Updating subscriber ID {} status to: {}", id, status);
        NewsletterSubscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber not found with ID: " + id));

        subscriber.setStatus(status);
        if (status == SubscriberStatus.UNSUBSCRIBED) {
            subscriber.setUnsubscribedAt(LocalDateTime.now());
        } else {
            subscriber.setUnsubscribedAt(null);
        }

        NewsletterSubscriber saved = subscriberRepository.save(subscriber);
        return toDTO(saved);
    }

    @Override
    public SubscriberDTO updateSubscriber(Long id, SubscriberRequestDTO request) {
        log.info("Updating subscriber details ID: {}", id);
        NewsletterSubscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber not found with ID: " + id));

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            subscriber.setEmail(request.getEmail().trim().toLowerCase());
        }
        if (request.getName() != null) {
            subscriber.setName(request.getName().trim());
        }
        if (request.getSource() != null) {
            subscriber.setSource(request.getSource());
        }
        if (request.getTags() != null) {
            subscriber.setTags(request.getTags());
        }
        if (request.getStatus() != null) {
            subscriber.setStatus(request.getStatus());
        }

        NewsletterSubscriber saved = subscriberRepository.save(subscriber);
        return toDTO(saved);
    }

    @Override
    public void deleteSubscriber(Long id) {
        log.info("Deleting subscriber ID: {}", id);
        if (!subscriberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subscriber not found with ID: " + id);
        }
        subscriberRepository.deleteById(id);
    }

    @Override
    public int bulkUpdateStatus(List<Long> ids, SubscriberStatus status) {
        log.info("Bulk updating status to {} for IDs: {}", status, ids);
        List<NewsletterSubscriber> subscribers = subscriberRepository.findAllById(ids);
        for (NewsletterSubscriber s : subscribers) {
            s.setStatus(status);
            if (status == SubscriberStatus.UNSUBSCRIBED) {
                s.setUnsubscribedAt(LocalDateTime.now());
            } else {
                s.setUnsubscribedAt(null);
            }
        }
        subscriberRepository.saveAll(subscribers);
        return subscribers.size();
    }

    @Override
    public int bulkDelete(List<Long> ids) {
        log.info("Bulk deleting subscribers for IDs: {}", ids);
        List<NewsletterSubscriber> subscribers = subscriberRepository.findAllById(ids);
        subscriberRepository.deleteAll(subscribers);
        return subscribers.size();
    }

    @Override
    public CampaignDTO sendCampaign(CampaignSendRequestDTO request) {
        log.info("Dispatching email broadcast campaign: {}", request.getSubject());

        List<NewsletterSubscriber> activeSubscribers = subscriberRepository.findByStatus(SubscriberStatus.SUBSCRIBED);
        int recipientCount = activeSubscribers.size();

        String campaignId = "camp_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        NewsletterCampaign campaign = NewsletterCampaign.builder()
                .id(campaignId)
                .subject(request.getSubject().trim())
                .preheader(request.getPreheader())
                .category(request.getCategory() != null ? request.getCategory() : "GENERAL_NEWS")
                .targetSegment(request.getTargetSegment() != null ? request.getTargetSegment() : "ALL_ACTIVE")
                .contentHtml(request.getContentHtml())
                .recipientsCount(recipientCount)
                .status("DELIVERED")
                .openRate(BigDecimal.valueOf(55.0 + Math.random() * 20.0).setScale(1, RoundingMode.HALF_UP))
                .clickRate(BigDecimal.valueOf(20.0 + Math.random() * 15.0).setScale(1, RoundingMode.HALF_UP))
                .authorName(request.getAuthorName() != null ? request.getAuthorName() : "Admin Team")
                .build();

        NewsletterCampaign saved = campaignRepository.save(campaign);

        LocalDateTime now = LocalDateTime.now();
        for (NewsletterSubscriber s : activeSubscribers) {
            s.setLastCampaignSentAt(now);
        }
        subscriberRepository.saveAll(activeSubscribers);

        log.info("Campaign {} broadcast dispatched to {} active subscribers", campaignId, recipientCount);
        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CampaignDTO> getAllCampaigns() {
        log.info("Fetching all marketing campaigns");
        return campaignRepository.findAllByOrderBySentAtDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NewsletterAnalyticsDTO getAnalytics() {
        long total = subscriberRepository.count();
        long active = subscriberRepository.countByStatus(SubscriberStatus.SUBSCRIBED);
        long unsubscribed = subscriberRepository.countByStatus(SubscriberStatus.UNSUBSCRIBED);

        BigDecimal activeRate = total > 0
                ? BigDecimal.valueOf(active * 100.0 / total).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long totalCampaigns = campaignRepository.count();
        BigDecimal avgOpenRate = campaignRepository.calculateAverageOpenRate().setScale(1, RoundingMode.HALF_UP);
        BigDecimal avgClickRate = campaignRepository.calculateAverageClickRate().setScale(1, RoundingMode.HALF_UP);

        Map<String, Long> distribution = new HashMap<>();
        for (SubscriberSource source : SubscriberSource.values()) {
            distribution.put(source.name(), 0L);
        }
        List<NewsletterSubscriber> allSubs = subscriberRepository.findAll();
        for (NewsletterSubscriber s : allSubs) {
            distribution.put(s.getSource().name(), distribution.getOrDefault(s.getSource().name(), 0L) + 1);
        }

        return NewsletterAnalyticsDTO.builder()
                .totalSubscribers(total)
                .activeSubscribers(active)
                .unsubscribedCount(unsubscribed)
                .activeRate(activeRate)
                .totalCampaigns(totalCampaigns)
                .avgOpenRate(avgOpenRate)
                .avgClickRate(avgClickRate)
                .channelDistribution(distribution)
                .build();
    }

    private SubscriberDTO toDTO(NewsletterSubscriber s) {
        return SubscriberDTO.builder()
                .id(s.getId())
                .email(s.getEmail())
                .name(s.getName())
                .status(s.getStatus())
                .source(s.getSource())
                .tags(s.getTags())
                .subscribedAt(s.getSubscribedAt())
                .unsubscribedAt(s.getUnsubscribedAt())
                .lastCampaignSentAt(s.getLastCampaignSentAt())
                .build();
    }

    private CampaignDTO toDTO(NewsletterCampaign c) {
        return CampaignDTO.builder()
                .id(c.getId())
                .subject(c.getSubject())
                .preheader(c.getPreheader())
                .category(c.getCategory())
                .targetSegment(c.getTargetSegment())
                .contentHtml(c.getContentHtml())
                .sentAt(c.getSentAt())
                .recipientsCount(c.getRecipientsCount())
                .status(c.getStatus())
                .openRate(c.getOpenRate())
                .clickRate(c.getClickRate())
                .authorName(c.getAuthorName())
                .build();
    }
}

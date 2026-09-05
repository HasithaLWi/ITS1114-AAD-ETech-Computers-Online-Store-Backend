package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.behavior.ProductBehaviorHistoryDTO;
import lk.ijse.etechbackend.dto.behavior.ProductBehaviorHistoryRequestDTO;
import lk.ijse.etechbackend.entity.Product;
import lk.ijse.etechbackend.entity.ProductBehaviorHistory;
import lk.ijse.etechbackend.enumiration.ProductBehaviorActor;
import lk.ijse.etechbackend.exception.ResourceNotFoundException;
import lk.ijse.etechbackend.repository.ProductBehaviorHistoryRepository;
import lk.ijse.etechbackend.repository.ProductRepository;
import lk.ijse.etechbackend.service.ProductBehaviorHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductBehaviorHistoryServiceImpl implements ProductBehaviorHistoryService {

    private final ProductBehaviorHistoryRepository historyRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductBehaviorHistoryDTO> getGlobalHistory(int page, int size) {
        log.info("Fetching global product behavior audit logs (page={}, size={})", page, size);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProductBehaviorHistory> historyPage = historyRepository.findAllByOrderByCreatedAtDesc(pageRequest);

        return historyPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductBehaviorHistoryDTO> getProductHistory(Long productId) {
        log.info("Fetching product behavior history for product ID: {}", productId);
        return historyRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductBehaviorHistoryDTO logEvent(ProductBehaviorHistoryRequestDTO request) {
        log.info("Logging behavior history for product ID {}: {}", request.getProductId(), request.getEventType());
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.getProductId()));

        String eventId = "pbe-" + Instant.now().getEpochSecond() + "-" + (int)(Math.random() * 900 + 100);

        ProductBehaviorHistory history = ProductBehaviorHistory.builder()
                .id(eventId)
                .product(product)
                .productName(request.getProductName() != null ? request.getProductName() : product.getName())
                .eventType(request.getEventType())
                .previousValue(request.getPreviousValue())
                .newValue(request.getNewValue())
                .triggerReason(request.getTriggerReason())
                .metricsSnapshot(request.getMetricsSnapshot())
                .actor(request.getActor() != null ? request.getActor() : ProductBehaviorActor.SYSTEM_AUTO_RULE)
                .build();

        ProductBehaviorHistory saved = historyRepository.save(history);
        return toDTO(saved);
    }

    private ProductBehaviorHistoryDTO toDTO(ProductBehaviorHistory h) {
        return ProductBehaviorHistoryDTO.builder()
                .id(h.getId())
                .productId(h.getProduct() != null ? h.getProduct().getId() : null)
                .productName(h.getProductName())
                .eventType(h.getEventType())
                .previousValue(h.getPreviousValue())
                .newValue(h.getNewValue())
                .triggerReason(h.getTriggerReason())
                .metricsSnapshot(h.getMetricsSnapshot())
                .actor(h.getActor())
                .createdAt(h.getCreatedAt())
                .build();
    }
}

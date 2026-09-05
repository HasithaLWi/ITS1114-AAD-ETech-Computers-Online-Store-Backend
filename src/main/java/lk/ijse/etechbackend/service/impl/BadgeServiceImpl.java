package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.badgedto.BadgeRequestDTO;
import lk.ijse.etechbackend.dto.badgedto.BadgeResponseDTO;
import lk.ijse.etechbackend.entity.Badge;
import lk.ijse.etechbackend.entity.Product;
import lk.ijse.etechbackend.enumiration.BadgeRuleType;
import lk.ijse.etechbackend.enumiration.Status;
import lk.ijse.etechbackend.exception.BadRequestException;
import lk.ijse.etechbackend.exception.ResourceNotFoundException;
import lk.ijse.etechbackend.repository.BadgeRepository;
import lk.ijse.etechbackend.repository.ProductRepository;
import lk.ijse.etechbackend.service.BadgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;
    private final ProductRepository productRepository;
    private final lk.ijse.etechbackend.repository.ProductBehaviorHistoryRepository behaviorHistoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BadgeResponseDTO> findAll() {
        log.info("Fetching all badges ordered by priority");
        List<Badge> badges = badgeRepository.findAllByOrderByPriorityAsc();
        if (badges.isEmpty()) {
            throw new ResourceNotFoundException("No badges found");
        }
        return badges.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BadgeResponseDTO> findActive() {
        log.info("Fetching all active badges ordered by priority");
        List<Badge> badges = badgeRepository.findByActiveOrderByPriorityAsc();
        return badges.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BadgeResponseDTO findById(String id) {
        log.info("Fetching badge by ID: {}", id);
        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Badge with id " + id + " not found"));
        return toResponseDTO(badge);
    }

    @Override
    @Transactional(readOnly = true)
    public BadgeResponseDTO findBySlug(String slug) {
        log.info("Fetching badge by slug: {}", slug);
        Badge badge = badgeRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Badge with slug " + slug + " not found"));
        return toResponseDTO(badge);
    }

    @Override
    @Transactional(readOnly = true)
    public BadgeResponseDTO findByName(String name) {
        log.info("Fetching badge by name: {}", name);
        Badge badge = badgeRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Badge with name " + name + " not found"));
        return toResponseDTO(badge);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BadgeResponseDTO> filterBadge(String search) {
        log.info("Filtering badges with search term: {}", search);
        if (search == null || search.isBlank()) {
            return findAll();
        }
        List<Badge> badges = badgeRepository.findAllBySearch(search.trim());
        return badges.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BadgeResponseDTO> filterBadgeByStatus(String status) {
        Status enumStatus;
        try {
            enumStatus = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status value: " + status);
        }
        List<Badge> badges = badgeRepository.findAllByStatus(enumStatus);
        return badges.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void createBadge(BadgeRequestDTO request) {
        log.info("Creating badge with name: {}", request.getName());

        String slug = request.getSlug() != null && !request.getSlug().isBlank()
                ? request.getSlug().trim().toLowerCase()
                : request.getName().trim().toLowerCase().replaceAll("[^a-z0-9]+", "-");

        String badgeId = request.getId() != null && !request.getId().isBlank()
                ? request.getId().trim()
                : "bdg-" + slug;

        if (badgeRepository.existsById(badgeId)) {
            throw new BadRequestException("Badge with id " + badgeId + " already exists");
        }
        if (badgeRepository.existsBySlug(slug)) {
            throw new BadRequestException("Badge with slug " + slug + " already exists");
        }
        if (badgeRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new BadRequestException("Badge with name " + request.getName() + " already exists");
        }

        Badge badge = Badge.builder()
                .id(badgeId)
                .name(request.getName().trim())
                .slug(slug)
                .colorKey(request.getColorKey() != null ? request.getColorKey() : "blue")
                .colorHex(request.getColorHex() != null ? request.getColorHex() : "#2563eb")
                .purpose(request.getPurpose())
                .standardDescription(request.getStandardDescription())
                .ruleType(request.getRuleType() != null ? request.getRuleType() : BadgeRuleType.manual)
                .criteria(request.getCriteria() != null ? request.getCriteria() : "custom")
                .priority(request.getPriority() != null ? request.getPriority() : 10)
                .isSystemDefault(request.getIsSystemDefault() != null ? request.getIsSystemDefault() : false)
                .canEdit(request.getCanEdit() != null ? request.getCanEdit() : true)
                .canDelete(request.getCanDelete() != null ? request.getCanDelete() : true)
                .status(request.getStatus() != null ? request.getStatus() : Status.ACTIVE)
                .build();

        badgeRepository.save(badge);
        log.info("Successfully created badge with ID: {}", badge.getId());
    }

    @Override
    public void updateBadge(String id, BadgeRequestDTO request) {
        log.info("Updating badge with ID: {}", id);

        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Badge with id " + id + " not found"));

        if (Boolean.FALSE.equals(badge.getCanEdit())) {
            throw new BadRequestException("Badge '" + badge.getName() + "' is protected and cannot be edited");
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            String trimmedName = request.getName().trim();
            if (badgeRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
                throw new BadRequestException("Badge with name '" + trimmedName + "' is already in use by another badge");
            }
            badge.setName(trimmedName);
        }

        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            String trimmedSlug = request.getSlug().trim().toLowerCase();
            if (badgeRepository.existsBySlugAndIdNot(trimmedSlug, id)) {
                throw new BadRequestException("Badge with slug '" + trimmedSlug + "' is already in use by another badge");
            }
            badge.setSlug(trimmedSlug);
        }

        if (request.getColorKey() != null) {
            badge.setColorKey(request.getColorKey());
        }
        if (request.getColorHex() != null) {
            badge.setColorHex(request.getColorHex());
        }
        if (request.getPurpose() != null) {
            badge.setPurpose(request.getPurpose());
        }
        if (request.getStandardDescription() != null) {
            badge.setStandardDescription(request.getStandardDescription());
        }
        if (request.getRuleType() != null) {
            badge.setRuleType(request.getRuleType());
        }
        if (request.getCriteria() != null) {
            badge.setCriteria(request.getCriteria());
        }
        if (request.getPriority() != null) {
            badge.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            badge.setStatus(request.getStatus());
        }
        if (request.getCanDelete() != null) {
            badge.setCanDelete(request.getCanDelete());
        }

        badgeRepository.save(badge);
        log.info("Successfully updated badge with ID: {}", badge.getId());
    }

    @Override
    public void updateStatus(String id, String status) {
        log.info("Updating status for badge ID {} to {}: {}", id, status, status);
        Optional<Badge> optionalBadge = badgeRepository.findById(id);
        if (optionalBadge.isEmpty()) {
            throw new ResourceNotFoundException("Badge with id " + id + " not found");
        }
        Badge badge = optionalBadge.get();
        try{
            Status newStatus = Status.valueOf(status.toUpperCase());
            badge.setStatus(newStatus);
            badgeRepository.save(badge);
        }catch (IllegalArgumentException e){
            throw new BadRequestException("Invalid status value: " + status);
        }

    }

    @Override
    public void deleteBadge(String id) {
        log.info("Soft deleting badge ID: {}", id);
        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Badge with id " + id + " not found"));

        if (Boolean.FALSE.equals(badge.getCanDelete())) {
            throw new BadRequestException("Badge '" + badge.getName() + "' is protected and cannot be deleted");
        }

        badge.setStatus(Status.DELETED);
        badgeRepository.save(badge);
    }

    @Override
    public void permanentDelete(String id) {
        log.info("Permanently deleting badge ID: {}", id);
        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Badge with id " + id + " not found"));

        if (Boolean.FALSE.equals(badge.getCanDelete())) {
            throw new BadRequestException("Badge '" + badge.getName() + "' is protected and cannot be permanently deleted");
        }

        if (badge.getProducts() != null && !badge.getProducts().isEmpty()) {
            for (Product product : badge.getProducts()) {
                product.setBadge(null);
            }
            productRepository.saveAll(badge.getProducts());
        }

        badgeRepository.delete(badge);
        log.info("Permanently deleted badge with ID: {}", id);
    }

    @Override
    public lk.ijse.etechbackend.dto.badgedto.BadgeAutoAssignResultDTO autoAssignBadges() {
        log.info("Executing automated rules engine for badge assignments");
        List<Product> products = productRepository.findAll();
        List<lk.ijse.etechbackend.dto.badgedto.BadgeChangeDTO> changes = new java.util.ArrayList<>();
        int evaluatedCount = 0;
        int assignedCount = 0;

        Badge topRatedBadge = badgeRepository.findBySlug("toprated").orElse(null);
        Badge bestsellerBadge = badgeRepository.findBySlug("bestseller").orElse(null);

        for (Product product : products) {
            if (product.getProductStatus() == Status.DELETED) continue;
            evaluatedCount++;

            Badge currentBadge = product.getBadge();
            String oldBadgeName = currentBadge != null ? currentBadge.getName() : "None";
            Badge newBadge = null;
            String reason = null;

            // Don't override Hot Deal system badges automatically
            if (currentBadge != null && "hotdeal".equalsIgnoreCase(currentBadge.getSlug())) {
                continue;
            }

            if (product.getRating() != null && product.getRating().compareTo(java.math.BigDecimal.valueOf(4.8)) >= 0
                    && product.getReviewsCount() != null && product.getReviewsCount() >= 10 && topRatedBadge != null) {
                newBadge = topRatedBadge;
                reason = "Reached " + product.getReviewsCount() + " reviews with " + product.getRating() + " star rating";
            } else if (product.getReviewsCount() != null && product.getReviewsCount() >= 50 && bestsellerBadge != null) {
                newBadge = bestsellerBadge;
                reason = "High sales / order volume with " + product.getReviewsCount() + "+ reviews";
            }

            if (newBadge != null && (currentBadge == null || !currentBadge.getId().equals(newBadge.getId()))) {
                product.setBadge(newBadge);
                productRepository.save(product);
                assignedCount++;

                changes.add(lk.ijse.etechbackend.dto.badgedto.BadgeChangeDTO.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .oldBadge(oldBadgeName)
                        .newBadge(newBadge.getName())
                        .reason(reason)
                        .build());

                // Log audit behavior history
                try {
                    String eventId = "pbe-" + java.time.Instant.now().getEpochSecond() + "-" + (int)(Math.random() * 900 + 100);
                    lk.ijse.etechbackend.entity.ProductBehaviorHistory history = lk.ijse.etechbackend.entity.ProductBehaviorHistory.builder()
                            .id(eventId)
                            .product(product)
                            .productName(product.getName())
                            .eventType(lk.ijse.etechbackend.enumiration.ProductBehaviorEventType.BADGE_AUTO_ASSIGNED)
                            .previousValue(oldBadgeName)
                            .newValue(newBadge.getName())
                            .triggerReason(reason)
                            .actor(lk.ijse.etechbackend.enumiration.ProductBehaviorActor.SYSTEM_AUTO_RULE)
                            .build();
                    behaviorHistoryRepository.save(history);
                } catch (Exception e) {
                    log.warn("Could not write behavior history log: {}", e.getMessage());
                }
            }
        }

        log.info("Automated rules engine evaluated {} products, assigned {} badges", evaluatedCount, assignedCount);
        return lk.ijse.etechbackend.dto.badgedto.BadgeAutoAssignResultDTO.builder()
                .evaluatedCount(evaluatedCount)
                .assignedCount(assignedCount)
                .changes(changes)
                .build();
    }

    private BadgeResponseDTO toResponseDTO(Badge badge) {
        return BadgeResponseDTO.builder()
                .id(badge.getId())
                .name(badge.getName())
                .slug(badge.getSlug())
                .colorKey(badge.getColorKey())
                .colorHex(badge.getColorHex())
                .purpose(badge.getPurpose())
                .standardDescription(badge.getStandardDescription())
                .ruleType(badge.getRuleType())
                .criteria(badge.getCriteria())
                .priority(badge.getPriority())
                .isSystemDefault(badge.getIsSystemDefault())
                .canEdit(badge.getCanEdit())
                .canDelete(badge.getCanDelete())
                .status(badge.getStatus())
                .createdAt(badge.getCreatedAt())
                .updatedAt(badge.getUpdatedAt())
                .build();
    }
}

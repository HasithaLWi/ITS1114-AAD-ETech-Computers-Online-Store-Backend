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

package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.productsdto.ProductResponseDTO;
import lk.ijse.etechbackend.dto.promotion.*;
import lk.ijse.etechbackend.entity.*;
import lk.ijse.etechbackend.exception.BadRequestException;
import lk.ijse.etechbackend.exception.ResourceNotFoundException;
import lk.ijse.etechbackend.repository.*;
import lk.ijse.etechbackend.service.ProductService;
import lk.ijse.etechbackend.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PromotionServiceImpl implements PromotionService {

    private final HotDealRepository hotDealRepository;
    private final HomeDealBannerRepository bannerRepository;
    private final DealBundleRepository bundleRepository;
    private final BundleItemRepository bundleItemRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    // --- HOT DEALS ---

    @Override
    @Transactional(readOnly = true)
    public List<HotDealResponseDTO> getHotDeals(Boolean activeOnly) {
        log.info("Fetching hot deals (activeOnly={})", activeOnly);
        List<HotDeal> deals = (activeOnly != null && activeOnly)
                ? hotDealRepository.findByIsActiveTrue()
                : hotDealRepository.findAll();

        return deals.stream().map(this::toHotDealDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HotDealResponseDTO getHotDealById(Long id) {
        log.info("Fetching hot deal by ID: {}", id);
        HotDeal deal = hotDealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hot deal not found with ID: " + id));
        return toHotDealDTO(deal);
    }

    @Override
    public HotDealResponseDTO createHotDeal(HotDealRequestDTO request) {
        log.info("Creating hot deal for product ID: {}", request.getProductId());
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.getProductId()));

        if (hotDealRepository.existsByProductId(product.getId())) {
            throw new BadRequestException("Hot deal already exists for product ID: " + product.getId());
        }

        BigDecimal origPrice = request.getOriginalPrice() != null ? request.getOriginalPrice() : product.getOriginalPrice();
        BigDecimal promoPrice = request.getPromoPrice() != null ? request.getPromoPrice() : product.getPrice();

        int discountPercent = request.getDiscountPercent() != null ? request.getDiscountPercent() : 0;
        if (discountPercent == 0 && origPrice != null && origPrice.compareTo(BigDecimal.ZERO) > 0 && promoPrice != null) {
            BigDecimal diff = origPrice.subtract(promoPrice);
            discountPercent = diff.multiply(BigDecimal.valueOf(100)).divide(origPrice, 0, RoundingMode.HALF_UP).intValue();
        }

        HotDeal hotDeal = HotDeal.builder()
                .product(product)
                .badge(request.getBadge() != null ? request.getBadge() : "HOT DEAL")
                .promoPrice(promoPrice)
                .originalPrice(origPrice)
                .discountPercent(discountPercent)
                .durationSeconds(request.getDurationSeconds() != null ? request.getDurationSeconds() : 86400)
                .timerUpdatedAt(LocalDateTime.now())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        HotDeal saved = hotDealRepository.save(hotDeal);
        return toHotDealDTO(saved);
    }

    @Override
    public HotDealResponseDTO updateHotDeal(Long id, HotDealRequestDTO request) {
        log.info("Updating hot deal ID: {}", id);
        HotDeal hotDeal = hotDealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hot deal not found with ID: " + id));

        if (request.getBadge() != null) hotDeal.setBadge(request.getBadge());
        if (request.getPromoPrice() != null) hotDeal.setPromoPrice(request.getPromoPrice());
        if (request.getOriginalPrice() != null) hotDeal.setOriginalPrice(request.getOriginalPrice());
        if (request.getDiscountPercent() != null) hotDeal.setDiscountPercent(request.getDiscountPercent());
        if (request.getDurationSeconds() != null) {
            hotDeal.setDurationSeconds(request.getDurationSeconds());
            hotDeal.setTimerUpdatedAt(LocalDateTime.now());
        }
        if (request.getIsActive() != null) hotDeal.setIsActive(request.getIsActive());

        HotDeal saved = hotDealRepository.save(hotDeal);
        return toHotDealDTO(saved);
    }

    @Override
    public void deleteHotDeal(Long id) {
        log.info("Deleting hot deal ID: {}", id);
        if (!hotDealRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hot deal not found with ID: " + id);
        }
        hotDealRepository.deleteById(id);
    }

    // --- HOME DEAL BANNER ---

    @Override
    @Transactional(readOnly = true)
    public HomeDealBannerDTO getHomeBanner() {
        log.info("Fetching home deal hero banner");
        HomeDealBanner banner = bannerRepository.findById(1).orElseGet(() -> {
            HomeDealBanner defaultBanner = HomeDealBanner.builder()
                    .id(1)
                    .dealTag("WEEKEND TECH DEAL")
                    .heading("Dominate Next-Gen Gaming With Apex Hardware")
                    .subtitle("Unlock massive discounts on high-performance rigs, RTX 40-Series GPUs, and OLED displays.")
                    .buttonText("View All Deals")
                    .buttonUrl("#deals")
                    .backgroundImage("https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=1600&auto=format&fit=crop&q=80")
                    .durationSeconds(86400)
                    .timerUpdatedAt(LocalDateTime.now())
                    .isActive(true)
                    .build();
            return bannerRepository.save(defaultBanner);
        });

        return toBannerDTO(banner);
    }

    @Override
    public HomeDealBannerDTO updateHomeBanner(HomeDealBannerDTO request) {
        log.info("Updating home deal hero banner");
        HomeDealBanner banner = bannerRepository.findById(1).orElseGet(() -> HomeDealBanner.builder().id(1).build());

        if (request.getDealTag() != null) banner.setDealTag(request.getDealTag());
        if (request.getHeading() != null) banner.setHeading(request.getHeading());
        if (request.getSubtitle() != null) banner.setSubtitle(request.getSubtitle());
        if (request.getButtonText() != null) banner.setButtonText(request.getButtonText());
        if (request.getButtonUrl() != null) banner.setButtonUrl(request.getButtonUrl());
        if (request.getBackgroundImage() != null) banner.setBackgroundImage(request.getBackgroundImage());
        if (request.getDurationSeconds() != null) {
            banner.setDurationSeconds(request.getDurationSeconds());
            banner.setTimerUpdatedAt(LocalDateTime.now());
        }
        if (request.getIsActive() != null) banner.setIsActive(request.getIsActive());

        HomeDealBanner saved = bannerRepository.save(banner);
        return toBannerDTO(saved);
    }

    // --- DEAL BUNDLES ---

    @Override
    @Transactional(readOnly = true)
    public List<DealBundleResponseDTO> getBundles(Boolean activeOnly) {
        log.info("Fetching deal bundles (activeOnly={})", activeOnly);
        List<DealBundle> bundles = (activeOnly != null && activeOnly)
                ? bundleRepository.findByIsActiveTrue()
                : bundleRepository.findAll();

        return bundles.stream().map(this::toBundleDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DealBundleResponseDTO getBundleById(Long id) {
        log.info("Fetching deal bundle by ID: {}", id);
        DealBundle bundle = bundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deal bundle not found with ID: " + id));
        return toBundleDTO(bundle);
    }

    @Override
    public DealBundleResponseDTO createBundle(DealBundleRequestDTO request) {
        log.info("Creating deal bundle: {}", request.getTitle());

        BigDecimal calculatedOriginalPrice = BigDecimal.ZERO;
        if (request.getBundleItems() != null && !request.getBundleItems().isEmpty()) {
            for (BundleItemRequestDTO itemReq : request.getBundleItems()) {
                Product p = productRepository.findById(itemReq.getProductId()).orElse(null);
                if (p != null) {
                    int qty = itemReq.getQuantity() != null ? itemReq.getQuantity() : 1;
                    calculatedOriginalPrice = calculatedOriginalPrice.add(p.getPrice().multiply(BigDecimal.valueOf(qty)));
                }
            }
        }

        BigDecimal origPrice = request.getOriginalPrice() != null ? request.getOriginalPrice() : calculatedOriginalPrice;

        DealBundle bundle = DealBundle.builder()
                .badge(request.getBadge() != null ? request.getBadge() : "BEST DEAL")
                .eyebrow(request.getEyebrow() != null ? request.getEyebrow() : "FEATURED DEAL")
                .title(request.getTitle().trim())
                .subtitle(request.getSubtitle())
                .imageUrl(request.getImageUrl())
                .price(request.getPrice())
                .originalPrice(origPrice)
                .targetQuota(request.getTargetQuota() != null ? request.getTargetQuota() : 20)
                .soldCount(request.getSoldCount() != null ? request.getSoldCount() : 0)
                .durationSeconds(request.getDurationSeconds() != null ? request.getDurationSeconds() : 86400)
                .timerUpdatedAt(LocalDateTime.now())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        if (request.getBundleItems() != null) {
            int order = 0;
            for (BundleItemRequestDTO itemReq : request.getBundleItems()) {
                Product p = productRepository.findById(itemReq.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemReq.getProductId()));
                BundleItem item = BundleItem.builder()
                        .product(p)
                        .quantity(itemReq.getQuantity() != null ? itemReq.getQuantity() : 1)
                        .displayOrder(itemReq.getDisplayOrder() != null ? itemReq.getDisplayOrder() : order++)
                        .build();
                bundle.addBundleItem(item);
            }
        }

        DealBundle saved = bundleRepository.save(bundle);
        return toBundleDTO(saved);
    }

    @Override
    public DealBundleResponseDTO updateBundle(Long id, DealBundleRequestDTO request) {
        log.info("Updating deal bundle ID: {}", id);
        DealBundle bundle = bundleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deal bundle not found with ID: " + id));

        if (request.getBadge() != null) bundle.setBadge(request.getBadge());
        if (request.getEyebrow() != null) bundle.setEyebrow(request.getEyebrow());
        if (request.getTitle() != null) bundle.setTitle(request.getTitle().trim());
        if (request.getSubtitle() != null) bundle.setSubtitle(request.getSubtitle());
        if (request.getImageUrl() != null) bundle.setImageUrl(request.getImageUrl());
        if (request.getPrice() != null) bundle.setPrice(request.getPrice());
        if (request.getOriginalPrice() != null) bundle.setOriginalPrice(request.getOriginalPrice());
        if (request.getTargetQuota() != null) bundle.setTargetQuota(request.getTargetQuota());
        if (request.getSoldCount() != null) bundle.setSoldCount(request.getSoldCount());
        if (request.getDurationSeconds() != null) {
            bundle.setDurationSeconds(request.getDurationSeconds());
            bundle.setTimerUpdatedAt(LocalDateTime.now());
        }
        if (request.getIsActive() != null) bundle.setIsActive(request.getIsActive());

        if (request.getBundleItems() != null) {
            bundle.getBundleItems().clear();
            int order = 0;
            for (BundleItemRequestDTO itemReq : request.getBundleItems()) {
                Product p = productRepository.findById(itemReq.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemReq.getProductId()));
                BundleItem item = BundleItem.builder()
                        .product(p)
                        .quantity(itemReq.getQuantity() != null ? itemReq.getQuantity() : 1)
                        .displayOrder(itemReq.getDisplayOrder() != null ? itemReq.getDisplayOrder() : order++)
                        .build();
                bundle.addBundleItem(item);
            }
        }

        DealBundle saved = bundleRepository.save(bundle);
        return toBundleDTO(saved);
    }

    @Override
    public void deleteBundle(Long id) {
        log.info("Deleting deal bundle ID: {}", id);
        if (!bundleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Deal bundle not found with ID: " + id);
        }
        bundleRepository.deleteById(id);
    }

    // --- MAPPING HELPERS ---

    private HotDealResponseDTO toHotDealDTO(HotDeal d) {
        ProductResponseDTO prodDTO = null;
        if (d.getProduct() != null) {
            try {
                prodDTO = productService.getProductById(d.getProduct().getId());
            } catch (Exception e) {
                log.warn("Could not load full ProductResponseDTO for hot deal product ID {}: {}", d.getProduct().getId(), e.getMessage());
            }
        }

        return HotDealResponseDTO.builder()
                .id(d.getId())
                .productId(d.getProduct() != null ? d.getProduct().getId() : null)
                .product(prodDTO)
                .badge(d.getBadge())
                .promoPrice(d.getPromoPrice())
                .originalPrice(d.getOriginalPrice())
                .discountPercent(d.getDiscountPercent())
                .durationSeconds(d.getDurationSeconds())
                .timerUpdatedAt(d.getTimerUpdatedAt())
                .isActive(d.getIsActive())
                .createdAt(d.getCreatedAt())
                .build();
    }

    private HomeDealBannerDTO toBannerDTO(HomeDealBanner b) {
        return HomeDealBannerDTO.builder()
                .id(b.getId())
                .dealTag(b.getDealTag())
                .heading(b.getHeading())
                .subtitle(b.getSubtitle())
                .buttonText(b.getButtonText())
                .buttonUrl(b.getButtonUrl())
                .backgroundImage(b.getBackgroundImage())
                .durationSeconds(b.getDurationSeconds())
                .timerUpdatedAt(b.getTimerUpdatedAt())
                .isActive(b.getIsActive())
                .build();
    }

    private DealBundleResponseDTO toBundleDTO(DealBundle b) {
        BigDecimal savingAmount = b.getOriginalPrice() != null && b.getPrice() != null
                ? b.getOriginalPrice().subtract(b.getPrice())
                : BigDecimal.ZERO;

        int savingPercent = (b.getOriginalPrice() != null && b.getOriginalPrice().compareTo(BigDecimal.ZERO) > 0 && savingAmount.compareTo(BigDecimal.ZERO) > 0)
                ? savingAmount.multiply(BigDecimal.valueOf(100)).divide(b.getOriginalPrice(), 0, RoundingMode.HALF_UP).intValue()
                : 0;

        // Bottleneck calculation:
        int stockLeft = Integer.MAX_VALUE;
        List<BundleItemDTO> itemsList = new ArrayList<>();

        if (b.getBundleItems() != null) {
            for (BundleItem bi : b.getBundleItems()) {
                Product p = bi.getProduct();
                if (p != null) {
                    int productStock = p.getBranchInventories() != null
                            ? p.getBranchInventories().stream().mapToInt(BranchInventory::getQuantity).sum()
                            : 0;

                    int qtyReq = bi.getQuantity() != null && bi.getQuantity() > 0 ? bi.getQuantity() : 1;
                    int maxBundlesPossible = productStock / qtyReq;
                    if (maxBundlesPossible < stockLeft) {
                        stockLeft = maxBundlesPossible;
                    }

                    Map<String, String> specsMap = new HashMap<>();
                    if (p.getSpecs() != null) {
                        for (Specs s : p.getSpecs()) {
                            specsMap.put(s.getName(), s.getDescription());
                        }
                    }

                    String image = p.getImages() != null && !p.getImages().isEmpty()
                            ? p.getImages().get(0).getImageUrl()
                            : null;

                    itemsList.add(BundleItemDTO.builder()
                            .id(bi.getId())
                            .productId(p.getId())
                            .name(p.getName())
                            .sku(p.getSku())
                            .qty(bi.getQuantity())
                            .unitPrice(p.getPrice())
                            .image(image)
                            .specs(specsMap)
                            .displayOrder(bi.getDisplayOrder())
                            .build());
                }
            }
        }

        if (stockLeft == Integer.MAX_VALUE) {
            stockLeft = 0;
        }

        int targetQuota = b.getTargetQuota() != null ? b.getTargetQuota() : 20;
        int soldCount = b.getSoldCount() != null ? b.getSoldCount() : 0;
        int totalQuota = targetQuota > 0 ? targetQuota : 20;
        int claimedPercent = Math.min(100, (int) Math.round((soldCount * 100.0) / totalQuota));

        return DealBundleResponseDTO.builder()
                .id(b.getId())
                .badge(b.getBadge())
                .eyebrow(b.getEyebrow())
                .title(b.getTitle())
                .subtitle(b.getSubtitle())
                .imageUrl(b.getImageUrl())
                .price(b.getPrice())
                .originalPrice(b.getOriginalPrice())
                .savingAmount(savingAmount)
                .savingPercent(savingPercent)
                .targetQuota(targetQuota)
                .soldCount(soldCount)
                .stockLeft(stockLeft)
                .claimedPercent(claimedPercent)
                .durationSeconds(b.getDurationSeconds())
                .timerUpdatedAt(b.getTimerUpdatedAt())
                .isActive(b.getIsActive())
                .componentsBreakdown(itemsList)
                .createdAt(b.getCreatedAt())
                .build();
    }
}
